package it.polito.tdp.meteo.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import it.polito.tdp.meteo.bean.Rilevamento;

public class MeteoDAO {

	public List<Rilevamento> getAllRilevamenti() {

		final String sql = "SELECT Localita, Data, Umidita FROM situazione ORDER BY data ASC";

		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();

		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				Rilevamento r = new Rilevamento(rs.getString("Localita"), rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(r);
			}

			conn.close();
			return rilevamenti;

		} catch (SQLException e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public List<Rilevamento> getAllRilevamentiLocalitaMese(int mese, String localita) {

		final String sql = "SELECT Data, Umidita FROM situazione WHERE localita = ? and  MONTH(Data) = ? ";
		
		List<Rilevamento> rilevamenti = new ArrayList<Rilevamento>();
		
		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setString(1, localita);
			st.setInt(2, mese);
			
			ResultSet rs = st.executeQuery();
			
			while (rs.next()) {
				Rilevamento rilevamento = new Rilevamento(localita, rs.getDate("Data"), rs.getInt("Umidita"));
				rilevamenti.add(rilevamento);
			}
			
			conn.close();
			return rilevamenti;
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public Double getAvgRilevamentiLocalitaMese(int mese, String localita) {

		List<Rilevamento> rilevamenti = this.getAllRilevamentiLocalitaMese(mese, localita);
		Double umidita = 0.0;
		int count = 0;
		
		for (Rilevamento r: rilevamenti) {
			umidita += r.getUmidita();
			count ++;
		}
		
		return (umidita/count);
	}

	public List<String> getAllLocalita() {
		
		final String sql = "SELECT DISTINCT Localita FROM Situazione";
		
		List<String> localita = new ArrayList<String>();
		
		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st;
			
			st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			
			while (rs.next()) {
				if(!localita.contains(rs.getString("Localita"))) {
						localita.add(rs.getString("Localita"));
				}
			}
			
			conn.close();
			return localita;
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}
	
	public Map<String,Double> getAvgRilevamentiMese (int mese) {
		
		final String sql = "SELECT localita, AVG(umidita) as umiditaMedia FROM situazione WHERE MONTH(Data) = ? GROUP BY localita";
		
		Map<String, Double> map = new TreeMap<String, Double>();
		
		try {
			Connection conn = DBConnect.getInstance().getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			
			st.setInt(1, mese);
			
			ResultSet rs = st.executeQuery();
			
			while (rs.next()) {
				map.put(rs.getString("localita"), rs.getDouble("umiditaMedia"));
			}
			
			conn.close();
			return map;
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		}
}
