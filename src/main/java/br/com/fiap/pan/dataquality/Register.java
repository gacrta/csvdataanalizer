package br.com.fiap.pan.dataquality;

import org.apache.spark.sql.Row;

public class Register {
	// identif_mask;modelo;score;restritivo;positivo;msg;anomesdia
	private long id;
	private String modelo;
	private long score;
	private long restritivo;
	private long positivo;
	private String msg;
	private long data;

}
