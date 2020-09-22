import torch
import torchvision
import onnx
from onnxsim import simplify

'''
https://pytorch.org/docs/stable/torchvision/models.html

normalize = transforms.Normalize(
    mean=[0.485, 0.456, 0.406],
    std=[0.229, 0.224, 0.225])
'''

MODEL_NAME = 'mobilenet_v2'

def save_simplify_onnx(model, example):
    # save
    torch.onnx.export(model, example, MODEL_NAME + ".onnx", input_names=["input"], output_names=["473"])
    # simplify
    model = onnx.load(MODEL_NAME + '.onnx')
    model_simp, check = simplify(model)
    assert check, "Simplified ONNX model could not be validated"
    onnx.save(model_simp, MODEL_NAME + ".onnx")

def save_pt(model, example):
    traced_script_module = torch.jit.trace(model, example)
    traced_script_module.save(MODEL_NAME + ".pt")

if __name__ == "__main__":
    model = torchvision.models.mobilenet_v2(pretrained=True)
    model.eval()
    example = torch.rand(1, 3, 224, 224)

    save_pt(model, example)
    save_simplify_onnx(model, example)
