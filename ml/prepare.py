import argparse
import csv
from glob import glob
import os
import pathlib
import splitfolders

# Specify the dataset location
data_location = pathlib.Path('ml/dataset')


def create_data_csv(filename, dataset):
    # Get a list of all the images in the dataset
    dataset = pathlib.Path(dataset).absolute()
    result = [y for x in os.walk(dataset) for y in glob(os.path.join(x[0], '*.jpg'))]

    # Create csv file
    with open(filename, 'w', newline='') as csvfile:
        label_writer = csv.writer(csvfile, delimiter=',', quotechar='"', quoting=csv.QUOTE_MINIMAL)
        for file in result:
            file = pathlib.Path(file)
            label_writer.writerow(['gs://cleanupourspace-vcm/img/dataset/Garbage classification/' + file.parts[-2] + '/' + file.parts[-1], file.parts[-2]])

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Process dataset preparation parameters.')
    parser.add_argument('--dataset', metavar='D', type=str,
                    help='location of dataset')

    parser.add_argument('--split', metavar='S', type=str,
                    help='splits dataset into train/test/val and store in supplied output folder')


    args = parser.parse_args()

    if args.dataset:
        DATASET = args.dataset
        create_data_csv('ml/dataset/all_classes.csv', DATASET)

    if args.split:
        OUTPUT = args.split
        if DATASET is None:
            print('Error: You must supply a dataset to split')
            exit(-1)
        # Split with a ratio.
        # To only split into training and validation set, set a tuple to `ratio`, i.e, `(.8, .2)`.
        splitfolders.ratio(DATASET, output=OUTPUT, seed=1337, ratio=(.7, .2, .1), group_prefix=None) # default values
