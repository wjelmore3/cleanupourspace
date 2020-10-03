import csv
from glob import glob
import os
import pathlib

# Specify the dataset location
data_location = pathlib.Path('ml/dataset')

# Get a list of all the images in the dataset
result = [y for x in os.walk(data_location) for y in glob(os.path.join(x[0], '*.jpg'))]

# Create csv file
with open('ml/dataset/all_data.csv', 'w') as csvfile:
    label_writer = csv.writer(csvfile, delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL)
    for file in result:
        file = pathlib.Path(file)
        label_writer.writerow(['gs://cleanupourspace-vcm/img/dataset/' + file.parts[-2] + '/' + file.parts[-1], file.parts[-2]])
