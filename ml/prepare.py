import splitfolders
import pathlib

data_location = pathlib.Path('ml/dataset')

# Split with a ratio.
# To only split into training and validation set, set a tuple to `ratio`, i.e, `(.8, .2)`.
splitfolders.ratio(data_location, output="ml/prepared_data", seed=1337, ratio=(.8, .1, .1), group_prefix=None) # default values
