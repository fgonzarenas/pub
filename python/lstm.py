import tensorflow as tf
import pandas as pd
import numpy as np
from tensorflow.python import keras
from sklearn.preprocessing import RobustScaler
import matplotlib.pyplot as plt

def create_dataset(X, y, time_steps=1):
	Xs, ys = [], []

	for i in range(len(X) - time_steps):
		v = X.iloc[i:(i + time_steps)].values
		Xs.append(v)
		ys.append(y.iloc[i + time_steps])

	return np.array(Xs), np.array(ys)

filename = 'data/traffic_data_oriented.csv'

# Parse data
data = pd.read_csv(filename, parse_dates=[0], index_col=0)

# Feature engineering
data['hour'] = data.index.hour
data['minute'] = data.index.minute

# Separate data
train_size = int(len(data) * 0.9)
test_size = len(data) - train_size
train, test = data.iloc[0:train_size], data.iloc[train_size:]

# Scale down features
f_columns = ['hour', 'minute']
f_transformer = RobustScaler().fit(train[f_columns].to_numpy())

train.loc[:, f_columns] = f_transformer.transform(train[f_columns].to_numpy())
test.loc[:, f_columns] = f_transformer.transform(test[f_columns].to_numpy())

# Scale down traffic
t_columns = [str(i) for i in range(data.shape[1] - len(f_columns))]
t_transformer = RobustScaler().fit(train[t_columns].to_numpy())

train[t_columns] = t_transformer.transform(train[t_columns].to_numpy())
test[t_columns] = t_transformer.transform(test[t_columns].to_numpy())

# Build dataset
time_steps = 10
X_train, y_train = create_dataset(train, train[t_columns], time_steps)
X_test, y_test = create_dataset(test, test[t_columns], time_steps)

# Build LSTM model
model = keras.Sequential()
model.add(
	keras.layers.Bidirectional(
		keras.layers.LSTM(
			units = 128,
			input_shape=(X_train.shape[1], X_train.shape[2])
		)
	)
)

model.add(keras.layers.Dropout(rate=0.2))
model.add(keras.layers.Dense(units=len(t_columns)))
model.add(keras.layers.ReLU())
model.compile(loss='mean_squared_error', optimizer='adam')

history = model.fit(
	X_train, y_train,
	epochs=5,
	batch_size=32,
	validation_split=0.1,
	shuffle=False
)

# Plot prediction outcome
prediction = model.predict(X_test)
prediction = t_transformer.inverse_transform(prediction)
prediction = np.round(prediction)
prediction = prediction.astype(int)

"""
for i in range(1):
	plt.figure()
	plt.plot(y_test[:,22], label='Real')
	plt.plot(prediction[:,22], label='Prediction')
	plt.title('Traffic prediction')
	plt.xlabel('Time step')
	plt.ylabel('Traffic')
	plt.legend()
	plt.show()
	#plt.savefig('results/edge'+str(i)+'.png')
	#plt.close()

# Plot loss by epoch
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

# Save model
"""
#model.save('model/traffic_model')
model.save_weights('model/traffic_model_weights')
json_string = model.to_json()
model_file = open('model/traffic_model_json', 'w')
model_file.write(json_string)
model_file.close()
"""

# Save predictions
timestamps = test.index[time_steps:]
columns = [i for i in range(prediction.shape[1])]

prediction = pd.DataFrame(prediction, index=timestamps, columns=columns)
real = pd.DataFrame(y_test.astype(int), index=timestamps, columns=columns)
prediction.index.name = 'timestamp'
real.index.name = 'timestamp'

prediction.to_csv('prediction.csv')
real.to_csv('real.csv')