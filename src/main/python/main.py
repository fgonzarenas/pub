import numpy as np
import pandas as pd

from roadPrediction import roadPrediction


def main():
    filepath = "../../../test.csv"
    roadPredict = roadPrediction(filepath)
    roadPredict.read_data()
    print(roadPredict.data)
    roadPredict.timestepDf(30)
    print("timestepData : \n", roadPredict.timestepData)
    roadPredict.visualization()
    X_train, y_train, X_test, y_test = roadPredict.preprocessing()
    roadPredict.fit(X_train, y_train)
    roadPredict.predict(X_test, y_test)


if __name__ == "__main__":
    main()