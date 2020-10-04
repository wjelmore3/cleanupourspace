import argparse
from pathlib import Path
import tensorflow as tf

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Convert the trained model to a tensorflow lite model')
    parser.add_argument('--model', metavar='M', type=str,
                    help='location of saved model to convert')
    parser.add_argument('--output', metavar='O', type=str,
                    help='location to save the converted model')

    args = parser.parse_args()

    if args.model and args.output:
        model_path = Path(args.model)

        # Create output path (if it does not exist already)
        out_path = Path(args.output)
        out_name = out_path.parts[-1]
        out_path.parents[0].mkdir(parents=True, exist_ok=True)
        out_path = out_path.parents[0] / out_name

        # Convert the model
        converter = tf.lite.TFLiteConverter.from_saved_model(model_path.as_posix())
        tflite_model = converter.convert()

        # Save the model
        with open(out_path.absolute().as_posix(), 'wb') as f:
            f.write(tflite_model)
    else:
        print('Error: Please supply a model to convert using --model <path to saved model>')
        exit()
