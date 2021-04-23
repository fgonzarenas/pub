package prediction;

import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.weights.WeightInit;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.learning.config.Adam;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.datasets.datavec.SequenceRecordReaderDataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.nd4j.linalg.dataset.DataSet;
import org.deeplearning4j.nn.conf.preprocessor.RnnToCnnPreProcessor;
import org.deeplearning4j.nn.conf.preprocessor.CnnToRnnPreProcessor;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.nd4j.evaluation.regression.RegressionEvaluation;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer.Builder;
import org.deeplearning4j.nn.conf.layers.ConvolutionLayer;
import org.deeplearning4j.nn.conf.Updater;
import org.deeplearning4j.nn.conf.layers.recurrent.Bidirectional;
import org.nd4j.linalg.api.ops.impl.layers.recurrent.config.LSTMConfiguration;

import java.io.FileNotFoundException;
import java.io.FileReader;  
import com.opencsv.CSVReader;

import org.datavec.api.records.reader.impl.csv.CSVSequenceRecordReader;
import org.datavec.api.records.reader.SequenceRecordReader;
import org.datavec.api.split.InputSplit;

import java.util.ArrayList;

public class PredictionLSTM 
{
	SequenceRecordReaderDataSetIterator train;
	SequenceRecordReaderDataSetIterator test;
	MultiLayerNetwork net;
	
	public PredictionLSTM(String filename)
	{
		
	}
	
	public void init(String filename)
	{
		read(filename);
		build();
	}
	
	public void read(String filename)
	{
		/*
		ArrayList<ArrayList<Integer>> samples = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> cur_sample;
		
		try   
		{
			CSVReader reader = new CSVReader(new FileReader(filename));
			String nextLine[];
			
			while((nextLine = reader.readNext()) != null)
			{
				cur_sample = new ArrayList<Integer>();
				
				for(String token : nextLine)
				{
					cur_sample.add(Integer.parseInt(token));
				}
				
				samples.add(cur_sample);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		*/
		
		
	}
	
	public MultiLayerNetwork build()
	{
		var LSTM_IN = 80;
		var LSTM_OUT = 128;
		var OUT = 10;
		
		var conf = new NeuralNetConfiguration.Builder()
				.optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                .seed(12345)
                .weightInit(WeightInit.XAVIER)
                .updater(new Adam(0.005))
                .list()
                .layer(0, new Bidirectional.Builder(null,null)
                	.mode(Bidirectional.Mode.AVERAGE)
	                .rnnLayer(new LSTM.Builder()
	                	.activation(Activation.SOFTSIGN)
	                    .nIn(LSTM_IN)
	                    .nOut(LSTM_OUT)
	                    .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
	                    .gradientNormalizationThreshold(10)
	                    .build())
	                .build()
	             )
	             .layer(1, new RnnOutputLayer.Builder(LossFunction.MSE)
	            		 .activation(Activation.IDENTITY)
	                     .nIn(LSTM_OUT)
	                     .nOut(OUT)
	                     .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
	                     .gradientNormalizationThreshold(10)
	                     .build())
	             .build();
			
	    var net = new MultiLayerNetwork(conf);
		net.init();
		
		return net;
	}
	
	public void run()
	{
		// Train model on training set
		net.fit(train , 25);
					
		// var eval = net.evaluateRegression[RegressionEvaluation](test);

		test.reset();

		// System.out.println(eval.stats());
	}
	
}
