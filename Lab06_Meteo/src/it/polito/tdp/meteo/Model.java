package it.polito.tdp.meteo;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.SimpleCity;
import it.polito.tdp.meteo.db.MeteoDAO;

public class Model {

	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;

	private List<Citta> citta;
	private MeteoDAO meteoDao;
	
	private double punteggioMiglioreSoluzione;
	private ArrayList<SimpleCity> miglioreSoluzione = null;
	
	public Model() {
		this.meteoDao = new MeteoDAO();
		this.citta = null;
	}
	
	/*
	 * Popolo l'ArrayList contenente le città
	 */
	private void popolaCitta () {
		List<String> localita = this.meteoDao.getAllLocalita();
		this.citta = new ArrayList<Citta>();
		
		// Aggiungo i nomi
		for (String nome: localita) {
			this.citta.add(new Citta(nome));
		}
	}

	public String getUmiditaMedia(int mese) {
		
		if(citta == null) {
			this.popolaCitta();
		}
		
		Map<String, Double> map = this.meteoDao.getAvgRilevamentiMese(mese);
		
		StringBuilder sb = new StringBuilder();
		
		
		for(String s: map.keySet()) {
				sb.append(String.format("%-12s ", s));
				sb.append(String.format("%-2f\n", map.get(s)));
		}
	
		return sb.toString();
	}

	private void resetCities (int mese) {
		for (Citta c: this.citta) {
			c.setCounter(0);
			c.setRilevamenti(meteoDao.getAllRilevamentiLocalitaMese(mese, c.getNome()));
		}
	}
	
	public String trovaSequenza(int mese) {
		
		if (this.citta == null) {
			this.popolaCitta();
		}
		
		this.punteggioMiglioreSoluzione = Double.MAX_VALUE;
		this.miglioreSoluzione = null;
		
		this.resetCities(mese);
		
		this.recoursive(new ArrayList<SimpleCity>(), 0);
		
		if (this.miglioreSoluzione != null) {
			String stringa = "Soluzione miglione trovata per mese: "
					+ mese + " con costo di: " + this.punteggioMiglioreSoluzione + "\n";
			return stringa + this.miglioreSoluzione.toString();
		}
		
		return "Nessuna soluzione trovata";
	}
	
	private void recoursive (List<SimpleCity> parziale, int step) {
		
		// Condizione di terminazione
		if ( step > this.NUMERO_GIORNI_TOTALI) {
			 double score = this.punteggioSoluzione(parziale);
			 
			 if( score < this.punteggioMiglioreSoluzione) {
				 this.miglioreSoluzione = new ArrayList<SimpleCity>(parziale);
				 this.punteggioMiglioreSoluzione = score;
			 }
			 return;
		}
		
		// Genera nuova soluzione parziale
		for (Citta c: this.citta) {
			SimpleCity sc = new SimpleCity(c.getNome(), c.getRilevamenti().get(step).getUmidita());
			
			parziale.add(sc);
			c.increaseCounter();
			
			if(this.controllaParziale(parziale)) {
				this.recoursive(parziale, step+1);
			}
			
			parziale.remove(step);
			c.decreaseCounter();
		}
	}

	private Double punteggioSoluzione(List<SimpleCity> soluzioneCandidata) {

		// Controllo che la lista non si nulla o vuota
		if (soluzioneCandidata == null || soluzioneCandidata.size() == 0) {
			return Double.MAX_VALUE;
		}
		
		// Controllo che la soluzione contenga tutte le città
		for (Citta c: this.citta) {
			if (!soluzioneCandidata.contains(new SimpleCity(c.getNome()))) {
				return Double.MAX_VALUE;
			}
		}
		
		double score = 0.0;
		SimpleCity previous = soluzioneCandidata.get(0);
		
		// Sommo tutti i costi delle città nella soluzione candidata
		for (SimpleCity sc: soluzioneCandidata) {
			if( !sc.equals(previous)) {
				score += this.COST;
			}
			previous = sc;
			score += sc.getCosto(); 
		}
		
		return score;
	}

	private boolean controllaParziale(List<SimpleCity> parziale) {

		// Se è nulla non è valida
		if (parziale == null) {
			return false;
		}
		
		// Se è d i dimensione zero è valida
		if (parziale.size() == 0) {
			return true;
		}
		
		// Controllo sul numero massimo di giorni consecutivi in una città
		for (Citta c: this.citta) {
			if (c.getCounter() > this.NUMERO_GIORNI_CITTA_MAX) {
				return false;
			}
		}
		
		// Controllo sul numero di giorni minimo consecutivi per città
		SimpleCity previous = parziale.get(0);
		int counter = 0;
		
		for (SimpleCity sc: parziale) {
			if( !previous.equals(sc)) {
				if(counter < this.NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN) {
					return false;
				}
				counter = 1;
				previous = sc;
			} else {
				counter ++;
			}
		}
		
		return true;
	}

}
