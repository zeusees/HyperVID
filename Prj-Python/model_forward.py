import cv2
import numpy as np


def softmax(x):
    """Compute softmax values for each sets of scores in x."""
    pass  # TODO: Compute and return softmax(x)
    x = np.array(x)
    x = np.exp(x)
    x.astype('float32')
    if x.ndim == 1:
        sumcol = sum(x)
        for i in range(x.size):
            x[i] = x[i]/float(sumcol)
    if x.ndim > 1:
        sumcol = x.sum(axis = 0)
        for row in x:
            for i in range(row.size):
                row[i] = row[i]/float(sumcol[i])
    return x

def softmax_2(x):
    return np.exp(x)/np.sum(np.exp(x),axis=0)


model = cv2.dnn.readNetFromCaffe("model/vehicle_model.prototxt","model/vehicle_model.caffemodel")

mean_rgb = [123.68, 116.779, 103.939]
std_rgb = [58.393, 57.12, 57.375]


aligned = cv2.imread("data/p1.jpg")
aligned = cv2.resize(aligned,(224,224))

im_tensor = np.zeros((1, 3, 224,224))
for i in range(3):
    im_tensor[0, i, :, :] = (aligned[:, :, 2 - i] / 1.0- mean_rgb[2 - i]) / std_rgb[2 - i]

model.setInput(im_tensor)
_embedding = model.forward()[0]

print(_embedding.transpose().max())
idx = _embedding.transpose().argmax()
print(idx)
label = open("label.txt","r")
label_txt =label.readlines()
print(label_txt[idx])
