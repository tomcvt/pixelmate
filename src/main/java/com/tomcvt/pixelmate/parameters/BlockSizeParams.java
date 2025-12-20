package com.tomcvt.pixelmate.parameters;

import java.util.List;
import java.util.Map;

import com.tomcvt.pixelmate.dto.ParamSpec;

public class BlockSizeParams implements OperationParameters {
    public static final String PARAM_SIZE = "blockSize";
    public static final String SIZE_TYPE = "Integer";
    public static final Integer DEFAULT_SIZE = 4;
    public static final Integer MIN_SIZE = 1;
    public static final Integer MAX_SIZE = 64;
    //TODO apply everywhere in params
    public static final BlockSizeParams DEFAULT_PARAMS = new BlockSizeParams(DEFAULT_SIZE);
    public static final ParamsFactory<BlockSizeParams> FACTORY = BlockSizeParams::fromMap;
    public static BlockSizeParams fromMap(Map<String, Object> params) {
        if (params == null) {
            return new BlockSizeParams(DEFAULT_SIZE);
        }
        Integer size = DEFAULT_SIZE;
        if (params.containsKey(PARAM_SIZE)) {
            try {
                size = Integer.parseInt(params.get(PARAM_SIZE).toString());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid block size value: " + params.get(PARAM_SIZE));
            }
            if (size < MIN_SIZE || size > MAX_SIZE) {
                throw new IllegalArgumentException("Block size value out of bounds: " + size + " : " + getRangeString());
            }
        }
        return new BlockSizeParams(size);
    }

    public static BlockSizeParams fromMapWithOldParams(BlockSizeParams oldParams, Map<String,Object> values) {
        if (values == null) {
            return oldParams;
        }
        Integer size = oldParams.getBlockSize();
        if (values.containsKey(PARAM_SIZE)) {
            try {
                size = Integer.parseInt(values.get(PARAM_SIZE).toString());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid block size value: " + values.get(PARAM_SIZE));
            }
            if (size < MIN_SIZE || size > MAX_SIZE) {
                throw new IllegalArgumentException("Block size value out of bounds: " + size + " : " + getRangeString());
            }
        }
        return new BlockSizeParams(size);
    }

    public static List<ParamSpec> getParamSpecs() {
        return List.of(
            new ParamSpec(PARAM_SIZE, SIZE_TYPE, DEFAULT_SIZE, MIN_SIZE, MAX_SIZE)
        );
    }

    private static String getRangeString() {
        return " [" + MIN_SIZE + " - " + MAX_SIZE + "]";
    }

    private final Integer blockSize;

    public BlockSizeParams(Integer blockSize) {
        this.blockSize = blockSize;
    }

    public Integer getBlockSize() {
        return blockSize;
    }
}
