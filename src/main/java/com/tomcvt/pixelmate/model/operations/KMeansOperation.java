package com.tomcvt.pixelmate.model.operations;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

import com.tomcvt.pixelmate.dto.ParamSpec;
import com.tomcvt.pixelmate.model.ImageFrame;
import com.tomcvt.pixelmate.model.ImageOperationI;
import com.tomcvt.pixelmate.model.OperationType;
import com.tomcvt.pixelmate.model.SimpleImageFrame;
import com.tomcvt.pixelmate.parameters.KMeansParams;
import com.tomcvt.pixelmate.parameters.KMeansParamsRestricted;
import com.tomcvt.pixelmate.utility.KMeansQuantizer;

public class KMeansOperation implements ImageOperationI<KMeansParams> {
    public static final String NAME = "KMEANS_CLUSTERING";
    public final OperationType operationType = OperationType.COLOR;
    private final long seed = 123456789L; // Fixed seed for reproducibility
    public KMeansOperation() {
    }

    public static KMeansParams createDefaultPipelineParams() {
        return KMeansParamsRestricted.FACTORY.fromMap(null);
    }
    @Override
    public OperationType getOperationType() {
        return operationType;
    }

    @Override
    public KMeansParams getDefaultPipelineParameters() {
        return createDefaultPipelineParams();
    }

    @Override
    public KMeansParams parsePipelineParameters(Map<String, Object> values) {
        return KMeansParamsRestricted.fromMap(values);
    }
    @Override
    public KMeansParams parsePipelineParameters(KMeansParams oldParams, Map<String, Object> values) {
        return KMeansParamsRestricted.fromMapWithOldParams(oldParams, values);
    }
    @Override
    public List<ParamSpec> getParamSpecs() {
        return KMeansParamsRestricted.getParamSpecs();
    }
    @Override
    public String getName() {
        return NAME;
    }
    @Override
    public ImageFrame apply(ImageFrame input, KMeansParams parameters) {
        BufferedImage inputImage = input.getConvertedBufferedImageForOperationByType(ImageFrame.ImageType.ARGB, ImageFrame.EditPath.COLOR);
        BufferedImage outputImage = KMeansQuantizer.quantize(inputImage,
                parameters.getK(),
                parameters.getMaxIterations(),
                parameters.getEps(),
                seed);
        return ImageFrame.with(input, outputImage, ImageFrame.ImageType.ARGB);
    }
    @Override
    public SimpleImageFrame applySimple(SimpleImageFrame input, KMeansParams parameters) {
        BufferedImage inputImage = input.getColoredImage();
        BufferedImage outputImage = KMeansQuantizer.quantize(inputImage,
                parameters.getK(),
                parameters.getMaxIterations(),
                parameters.getEps(),
                seed);
        return input.withColored(outputImage);
    }
}
