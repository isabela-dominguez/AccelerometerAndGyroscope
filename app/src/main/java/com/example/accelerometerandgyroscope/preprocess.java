package com.example.accelerometerandgyroscope;
import org.apache.commons.math3.transform.FastFourierTransformer;

// preprocessing FOR A SINGLE WINDOW AT A TIME
public class preprocess {
    public float[] features;    // 16 input features to feed into the network
    // OUTPUT ARRAY VALUE ORDER:
    // x, y, z accel mean; x, y, z gyro mean; accel SMA;
    // x, y, z accel SD; x, y, z gyro SD; x, y, z accel fft

    public preprocess(float[][] exerData) {
        means_and_SMA(exerData);
        std_devs(exerData);
        fast_fourier(exerData);
        normalize();
        // NORMALIZATION IS CURRENTLY USING MAX & MIN VALUES FROM TRAINING DATA
        // not too sure what to do about this...
        // potentially figure out the theoretical maximum and minimum for each field,
        // then reprocess the original data & retrain the network
    }

    private void means_and_SMA(float[][] exerData) {
        // for each line of data...
        for (int i = 0; i < exerData.length; i++) {
            // iterating through each input dimension and squaring it
            for (int j = 0; j < exerData[0].length; j++) {
                features[j] += java.lang.Math.pow(exerData[i][j], 2);
            }
            // finding signal-magnitude area (SMA)
            // sum of magnitude of all axes of acceleration
            features[6] += java.lang.Math.abs(exerData[i][0]) + java.lang.Math.abs(exerData[i][1]) + java.lang.Math.abs(exerData[i][2]);
        }
        // taking square root of the sum of each acceleration & gyroscope axis
        // to make each mean equal sqrt(Ax1^2 +Ax2^2 + Ax3^2 + Ax4^2), etc.
        for (int k = 0; k < exerData[0].length; k++) {
            features[k] = (float) java.lang.Math.sqrt(features[k]);
        }
    }

    private void std_devs(float[][] exerData) {
        // for each axis of acceleration & rotation...
        for (int i = 0; i < exerData[0].length; i++) {
            float[] temp = new float[exerData.length];
            float sum = 0;

            // store all the values of that axis within the window of data given
            // (makes life easier)
            for (int j = 0; j < exerData.length; j++) {
                temp[j] = exerData[j][i];
                sum += exerData[j][i];
            }
            // find mean over this axis (to use later)
            float mean = sum / exerData.length;

            // take standard deviation over this axis
            for (int k = 0; k < temp.length; k++) {
                features[i + 7] += java.lang.Math.pow((temp[k] - mean), 2);
            }
            features[i + 7] = (float) java.lang.Math.sqrt(features[i + 7] / exerData.length);
        }
    }

    private void fast_fourier(float[][] exerData) {
        FastFourierTransformer fastFourierTransformer = new FastFourierTransformer(org.apache.commons.math3.transform.DftNormalization.STANDARD);

        // for each axis of acceleration...
        for (int i = 0; i < 3; i++) {
            double[] temp = new double[exerData.length];

            // make an array of all the entries for this axis
            // (again for convenience)
            for (int j = 0; j < exerData.length; j++) {
                temp[j] = exerData[j][i];
            }

            // take the fast fourier transform over the entire axis
            org.apache.commons.math3.complex.Complex[] complex = fastFourierTransformer.transform(temp, org.apache.commons.math3.transform.TransformType.FORWARD);

            // get power spectrum for this axis
            for (int k = 0; k < complex.length; k++) {
                complex[k] = complex[k].pow(2);
            }

            // if there are less than 5 data values, just take what we have
            // otherwise, we'll take first 5 coefficients (as per literature study)
            int loops;
            if (complex.length < 5) {
                loops = complex.length;
            } else {
                loops = 5;
            }

            // get real components' absolute values & sum them for this axis
            for (int m = 0; m < loops; m++) {
                features[i + 14] += (float) complex[m].getReal();
            }
        }
    }

    private void normalize() {
        // normalized value = (value - minimum) / (maximum = minimum)

        // x acceleration mean (FIND MAX SENSOR VALUE?)
        features[0] = (features[0] - 0) / (124 - 0);
        // y acceleration mean (FIND MAX SENSOR VALUE?)
        features[1] = (features[1] - 0) / (97 - 0);
        // z acceleration mean (FIND MAX SENSOR VALUE?)
        features[2] = (features[2] - 0) / (104 - 0);
        // x gyroscope mean (FIND MAX SENSOR VALUE?)
        features[3] = (features[3] - 0) / (18 - 0);
        // y gyroscope mean (FIND MAX SENSOR VALUE?)
        features[4] = (features[4] - 0) / (13 - 0);
        // z gyroscope mean (FIND MAX SENSOR VALUE?)
        features[5] = (features[5] - 0) / (28 - 0);
        // acceleration SMA
        features[6] = (features[6] - 77) / (770 - 77);
        // x acceleration standard deviation
        features[7] = (features[7] - 0) / (34 - 0);
        // y acceleration standard deviation
        features[8] = (features[8] - 0) / (28 - 0);
        // z acceleration standard deviation
        features[9] = (features[9] - 0) / (30 - 0);
        // x gyroscope standard deviation
        features[10] = (features[10] - 0) / (5 - 0);
        // y gyroscope standard deviation
        features[11] = (features[11] - 0) / (4 - 0);
        // z gyroscope standard deviation
        features[12] = (features[12] - 0) / (8 - 0);
        // x acceleration fast fourier transform
        features[13] = (features[13] - 1) / (115594 - 1);
        // y acceleration fast fourier transform
        features[14] = (features[14] - 6) / (58576 - 6);
        // z acceleration fast fourier transform
        features[15] = (features[15] - 45) / (63472 - 45);
    }
}
