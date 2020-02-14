package ru.gordinmitya.mace;

public class ModelInfoNative {
    public final String pbPath;
    public final String dataPath;
    public final String inputName;
    public final String outputName;
    public final int[] inputShape;
    public final int[] outputShape;

    public ModelInfoNative(String pbPath, String dataPath, String inputName, String outputName, int[] inputShape, int[] outputShape) {
        this.pbPath = pbPath;
        this.dataPath = dataPath;
        this.inputName = inputName;
        this.outputName = outputName;
        this.inputShape = inputShape;
        this.outputShape = outputShape;
    }

    public static ModelInfoNative fromConvertedModel(ConvertedModel convertedModel, String pbPath, String dataPath) {
        int[] inputShape = new int[]{
                1,
                convertedModel.getModel().getInputSize().getSecond(),
                convertedModel.getModel().getInputSize().getFirst(),
                convertedModel.getModel().getInputChannels()
        };
        return new ModelInfoNative(
                pbPath,
                dataPath,
                convertedModel.getInputName(),
                convertedModel.getOutputName(),
                inputShape,
                convertedModel.getModel().getOutputShape()
        );
    }
}
