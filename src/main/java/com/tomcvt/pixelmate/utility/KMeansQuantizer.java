package com.tomcvt.pixelmate.utility;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Random;

public class KMeansQuantizer {

    public static BufferedImage quantize(BufferedImage src, int k, int maxIter, double EPS, long seed) {
        if (k <= 0) throw new IllegalArgumentException("k must be > 0");
        int w = src.getWidth();
        int h = src.getHeight();
        int n = w * h;

        // Read pixels into an array of int RGB (no alpha influence)
        int[] pixels = new int[n];
        src.getRGB(0, 0, w, h, pixels, 0, w);

        // Convert to double RGB vectors [r,g,b]
        double[][] points = new double[n][3];
        for (int i = 0; i < n; i++) {
            int rgb = pixels[i];
            int r = (rgb >> 16) & 0xFF;
            int g = (rgb >> 8) & 0xFF;
            int b = rgb & 0xFF;
            points[i][0] = r;
            points[i][1] = g;
            points[i][2] = b;
        }

        Random rnd = new Random(seed);

        // Initialize centroids with kmeans++
        double[][] centroids = kmeansPlusPlusInit(points, k, rnd);

        int[] assignments = new int[n];
        Arrays.fill(assignments, -1);

        double[][] newCentroids = new double[k][3];
        int[] counts = new int[k];

        //final double EPS = 0.5; // small threshold for centroid movement

        for (int iter = 0; iter < maxIter; iter++) {
            // Assignment step
            boolean changed = false;
            for (int i = 0; i < n; i++) {
                double minDist = Double.POSITIVE_INFINITY;
                int best = -1;
                double[] p = points[i];
                for (int c = 0; c < k; c++) {
                    double dx = p[0] - centroids[c][0];
                    double dy = p[1] - centroids[c][1];
                    double dz = p[2] - centroids[c][2];
                    double dist = dx*dx + dy*dy + dz*dz;
                    if (dist < minDist) {
                        minDist = dist;
                        best = c;
                    }
                }
                if (assignments[i] != best) {
                    changed = true;
                    assignments[i] = best;
                }
            }

            // If no assignment change, we can exit early (optional)
            // But centroids may still move if previous iteration had empty clusters; continue.

            // Update step: compute means
            for (int c = 0; c < k; c++) {
                Arrays.fill(newCentroids[c], 0.0);
                counts[c] = 0;
            }
            for (int i = 0; i < n; i++) {
                int a = assignments[i];
                double[] p = points[i];
                newCentroids[a][0] += p[0];
                newCentroids[a][1] += p[1];
                newCentroids[a][2] += p[2];
                counts[a]++;
            }
            // Handle empty clusters: reinit to a random point
            for (int c = 0; c < k; c++) {
                if (counts[c] == 0) {
                    // pick a random point as centroid
                    int idx = rnd.nextInt(n);
                    newCentroids[c][0] = points[idx][0];
                    newCentroids[c][1] = points[idx][1];
                    newCentroids[c][2] = points[idx][2];
                    counts[c] = 1; // avoid division by zero
                } else {
                    newCentroids[c][0] /= counts[c];
                    newCentroids[c][1] /= counts[c];
                    newCentroids[c][2] /= counts[c];
                }
            }

            // Check centroid movement (convergence)
            double maxMove = 0.0;
            for (int c = 0; c < k; c++) {
                double dx = centroids[c][0] - newCentroids[c][0];
                double dy = centroids[c][1] - newCentroids[c][1];
                double dz = centroids[c][2] - newCentroids[c][2];
                double move = Math.sqrt(dx*dx + dy*dy + dz*dz);
                if (move > maxMove) maxMove = move;
                centroids[c][0] = newCentroids[c][0];
                centroids[c][1] = newCentroids[c][1];
                centroids[c][2] = newCentroids[c][2];
            }

            if (!changed || maxMove < EPS) {
                // converged
                break;
            }
        }

        // Build output image by mapping each pixel to centroid color
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        int[] outPixels = new int[n];
        for (int i = 0; i < n; i++) {
            int c = assignments[i];
            int r = (int)Math.round(centroids[c][0]);
            int g = (int)Math.round(centroids[c][1]);
            int b = (int)Math.round(centroids[c][2]);
            int a = (pixels[i] >> 24) & 0xFF; // preserve original alpha if needed
            int outRgb = (a << 24) | (clamp(r) << 16) | (clamp(g) << 8) | clamp(b);
            outPixels[i] = outRgb;
        }
        out.setRGB(0, 0, w, h, outPixels, 0, w);
        return out;
    }

    private static int clamp(int v) {
        if (v < 0) return 0;
        if (v > 255) return 255;
        return v;
    }

    // kmeans++ initialization: deterministic-ish if seed set
    private static double[][] kmeansPlusPlusInit(double[][] points, int k, Random rnd) {
        int n = points.length;
        double[][] centroids = new double[k][3];

        // pick first centroid randomly
        int first = rnd.nextInt(n);
        centroids[0] = Arrays.copyOf(points[first], 3);

        double[] distSq = new double[n];

        for (int i = 1; i < k; i++) {
            double total = 0.0;
            for (int p = 0; p < n; p++) {
                double minD = Double.POSITIVE_INFINITY;
                for (int j = 0; j < i; j++) {
                    double dx = points[p][0] - centroids[j][0];
                    double dy = points[p][1] - centroids[j][1];
                    double dz = points[p][2] - centroids[j][2];
                    double d2 = dx*dx + dy*dy + dz*dz;
                    if (d2 < minD) minD = d2;
                }
                distSq[p] = minD;
                total += minD;
            }
            // pick next centroid weighted by distSq
            double r = rnd.nextDouble() * total;
            double cumulative = 0.0;
            int chosen = 0;
            for (int p = 0; p < n; p++) {
                cumulative += distSq[p];
                if (cumulative >= r) {
                    chosen = p;
                    break;
                }
            }
            centroids[i] = Arrays.copyOf(points[chosen], 3);
        }
        return centroids;
    }
}
/*

Pixel art / stylized graphics
k = 8–32
maxIter = 8–12
EPS = 1.0

Photos / screenshots
k = 16–64
maxIter = 15–20
EPS = 0.5

Palette generation for further use
k = 64–128
maxIter = 30
EPS = 0.25


Only if runtime doesn’t matter.

*/