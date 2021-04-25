import numpy as np
import pandas as pd
import matplotlib.pyplot as plt

from sklearn.preprocessing import RobustScaler
from tensorflow import keras

class roadPrediction(object):

    def __init__(self, filepath):
        self.filepath = filepath
        self.data = None
        self.timestepData = None
        self.cnt_transformer = None
        self.model = None

    def read_data(self):
        self.data = pd.read_csv(self.filepath, delimiter=";", parse_dates=True, index_col=0)
        self.data['day_of_week'] = self.data.index.dayofweek
        self.data['start_hour'] = self.data.index.hour
        self.data['day_of_month'] = self.data.index.day


    def timestepDf(self, timedelta):
        print("\n--- Generation of timestep dataframe ---")
        start = pd.Timestamp(self.data.index[0])
        end = pd.Timestamp(self.data.index[-1])
        firstTimestep = start.floor(freq = "min") 
        lastTimestep = end.floor(freq = "min")
        """
        print("start : ", start)
        print("firstTimestep : ", firstTimestep)
        print("lastTimestep : ", lastTimestep)
        print("lastTimestep + timedelta: ", lastTimestep + pd.Timedelta(minutes=timedelta))
        print("firstTimestep < lastTimestep : ", firstTimestep < lastTimestep)
        """
        self.timestepData = pd.DataFrame(columns=["timestep", "travelNumbers"])
        delta = pd.Timedelta(minutes=timedelta)

        stop = False
        i = 0
        row = 0
        currentTimestep = firstTimestep
        
        while (not stop and currentTimestep < lastTimestep):
            nextTimestep = currentTimestep + delta
            cnt = 0
            while self.data.index[i] < nextTimestep and i < len(self.data)-1:
                cnt += 1
                i += 1

            if i == len(self.data)-1:
                stop = True

            self.timestepData.loc[row] = [currentTimestep, cnt]
            currentTimestep = nextTimestep
            row += 1

        self.timestepData = self.timestepData.set_index('timestep')


    def create_dataset(self, X, y, time_steps=1):
        Xs, ys = [], []
        for i in range(len(X) - time_steps):
            v = X.iloc[i:(i + time_steps)].values
            Xs.append(v)
            ys.append(y.iloc[i + time_steps])
        return np.array(Xs), np.array(ys)


    def preprocessing(self):
        print("\n--- Preprocessing ---")
        train_size = int(len(self.timestepData) * 0.8)
        train, test = self.timestepData.iloc[0:train_size], self.timestepData.iloc[train_size:len(self.timestepData)]
        print("train : ", train)
        print("test : ", test)
        print("train : ", len(train), " | test : ", len(test))
        
        # Scaling data
        cnt_column = ['travelNumbers']
        self.cnt_transformer = RobustScaler().fit(train[cnt_column].to_numpy())
        train.loc[:, cnt_column] = self.cnt_transformer.transform(train[cnt_column].to_numpy())
        test.loc[:, cnt_column] = self.cnt_transformer.transform(test[cnt_column].to_numpy())

        time_steps = 10
        # reshape to [samples, time_steps, n_features]
        X_train, y_train = self.create_dataset(train, train['travelNumbers'], time_steps)
        X_test, y_test = self.create_dataset(test, test['travelNumbers'], time_steps)
        print(X_train.shape, y_train.shape)
        return X_train, y_train, X_test, y_test

    def visualization(self):
        # plot number of travel per hour
        hours = np.array([i for i in range(0,25)])
        count = []
        for h in hours:
            count.append(len(self.data[self.data['start_hour'] == h].index))
        count = np.array(count)

        plt.figure(figsize=(12,5))
        plt.plot(hours, count, "o-")
        plt.xlabel("hour")
        plt.ylabel("count")
        plt.xticks(hours)
        plt.show()

        # plot number of travel per month day
        day = np.array([i for i in range(1,31)])
        count = []
        for d in day:
            count.append(len(self.data[self.data['day_of_month'] == d].index))
        count = np.array(count)

        plt.figure(figsize=(12,5))
        plt.plot(day, count, "o-")
        plt.xlabel("day")
        plt.ylabel("count")
        plt.xticks(day)
        plt.ylim(bottom=0, top=np.max(count)*1.1)
        plt.show()
        
        # plot number of travel per timestep
        timestep = np.array([i for i in range(len(self.timestepData))])
        cnt = self.timestepData[["travelNumbers"]].to_numpy()

        plt.figure(figsize=(12,5))
        plt.plot(timestep, cnt)
        plt.xlabel("timestep")
        plt.ylabel("count")
        plt.show()


    def fit(self, X_train, y_train):        
        self.model = keras.Sequential()
        self.model.add(
            keras.layers.Bidirectional(
                keras.layers.LSTM(
                    units=128,
                    input_shape=(X_train.shape[1], X_train.shape[2])
                )
            )
        )
        self.model.add(keras.layers.Dropout(rate=0.2))
        self.model.add(keras.layers.Dense(units=1))
        self.model.compile(loss='mean_squared_error', optimizer='adam')

        X_train = np.asarray(X_train).astype('float32')
        y_train = np.asarray(y_train).astype('float32')

        history = self.model.fit(
            X_train, y_train,
            epochs=30,
            batch_size=32,
            validation_split=0.1,
            shuffle=False
        )
        
        # Plot loss by epoch
        """
        plt.figure()
        plt.plot(history.history['loss'], label='Train')
        plt.plot(history.history['val_loss'], label='Validate')
        plt.title('Loss per epoch')
        plt.xlabel('Epoch')
        plt.ylabel('Loss')
        plt.legend()
        plt.show()
        plt.close()
        """

    def predict(self, X_test, y_test):  
        X_test = np.asarray(X_test).astype('float32')
        prediction = self.model.predict(X_test)

        # Scalling
        prediction = self.cnt_transformer.inverse_transform(prediction)
        y_test = self.cnt_transformer.inverse_transform(y_test.reshape(-1, 1))
        # Affichage
        plt.figure()
        plt.plot(y_test, label='Real')
        plt.plot(prediction, label='Prediction')
        plt.xlabel("Timestep")
        plt.ylabel("Number of travel")
        plt.title("Prediction of the number of travel")
        plt.legend()
        plt.show()