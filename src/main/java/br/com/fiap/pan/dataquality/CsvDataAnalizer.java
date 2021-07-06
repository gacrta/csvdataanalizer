package br.com.fiap.pan.dataquality;

import scala.Function1;
import scala.Tuple2;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import com.amazon.deequ.analyzers.DataTypeInstances;

import com.amazon.deequ.profiles.ColumnProfile;
import com.amazon.deequ.profiles.ColumnProfilerRunner;
import com.amazon.deequ.profiles.ColumnProfiles;
import com.amazon.deequ.profiles.NumericColumnProfile;

public final class CsvDataAnalizer {
  private static final String filename = "C:\\Users\\gabri\\Downloads\\base_exemplo_score.csv";

  public static void main(String[] args) throws Exception {

	// Config do spark
	SparkConf sparkConf = new SparkConf()
			.setAppName("CsvDataAnalizer")
			.setMaster("local")
			.set("spark.executor.memory","2g");

	// Inicializa spark
    SparkSession spark = SparkSession
      .builder()
      .config(sparkConf)
      .getOrCreate();

    // Consome arquivo csv
    Dataset<Row> dataset = spark.read()
    		.option("sep", ";")
    		//.option("dateFormat", "yyyyMMdd") // Temos 1 ocorrencia de data invalida que impede o uso de anomesdia como DATE
    		.schema("identif_mask INT, modelo STRING, score INT, restritivo INT, positivo INT, msg STRING, anomesdia INT")
    		.csv(filename);

    // Cria perfil das colunas com deequ
    ColumnProfiles result = new ColumnProfilerRunner().onData(dataset).run();

    // Imprime perfil de cada coluna
    result.profiles().foreach(new Function1<Tuple2<String,ColumnProfile>, Void>() {
    	public Void apply(Tuple2<String,ColumnProfile> tuple) {
    		String columName = tuple._1;
    		ColumnProfile columnProfile = tuple._2;

    	    System.out.println("Column " + columName + ":\n" +
    	    		  "\tcompleteness: " + columnProfile.completeness() + "\n" +
    	    		  "\tapproximate number of distinct values: " + columnProfile.approximateNumDistinctValues() + "\n" +
    	    		  "\tdatatype: " + columnProfile.dataType() + "\n");

    		if(columnProfile.dataType() == DataTypeInstances.Integral()) {
    			NumericColumnProfile ncp = (NumericColumnProfile) columnProfile;
    			System.out.println("Statistics of " + columName+ ":\n" +
    		    	      "\tminimum: " + ncp.minimum().get() + "\n" +
    		    	      "\tmaximum: " + ncp.maximum().get() + "\n" +
    		    	      "\tmean: " + ncp.mean().get() + "\n" +
    		    	      "\tstandard deviation: " + ncp.stdDev().get() + "\n");
    		}
    		
    	    return null;
    	}
	});
        
    //TO DO: criar novo arquivo csv
    
    spark.stop();
  }
}
