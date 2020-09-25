import torch
import torchvision
from torchvision import models
from torchvision import transforms
import glob
from PIL import Image
import numpy as np


model = models.mobilenet_v2(pretrained=True)
model = model.eval().cpu()

images = glob.glob("*/*.jpeg")

preprocess = transforms.Compose([
    transforms.ToTensor(),
    transforms.Normalize(
        mean=[0.485, 0.456, 0.406],
        std=[0.229, 0.224, 0.225])
])

with open('labels.txt', 'r') as f:
    labels = f.readlines()

for path in images:
    image = Image.open(path)
    x = preprocess(image).unsqueeze(0)
    with torch.no_grad():
        y = model(x).numpy()[0]
    predicted = labels[y.argmax()].rstrip()
    print(path, predicted)
    assert path.split('/')[0] == predicted
    np.savetxt(path.replace('.jpeg', '.txt'), y)