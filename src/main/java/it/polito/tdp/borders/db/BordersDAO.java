package it.polito.tdp.borders.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.borders.model.Border;
import it.polito.tdp.borders.model.Country;

public class BordersDAO {

	public List<Country> loadAllCountries(Map <Integer, Country> idMap) {

		String sql = "SELECT ccode, StateAbb, StateNme FROM country ORDER BY StateAbb";
		List<Country> result = new ArrayList<Country>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("ccode");
				String abb = rs.getString("StateAbb");
				String nome = rs.getString("StateNme");
				
				//System.out.format("%d %s %s\n", rs.getInt("ccode"), rs.getString("StateAbb"), rs.getString("StateNme"));
				if(!idMap.containsKey(id)) {
					Country temp = new Country(id, abb, nome);
					idMap.put(id, temp);
				}
			}
			
			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<Country> getVertici(int anno, Map<Integer, Country> idMap){
		String sql = "SELECT DISTINCT state1no AS id1, state1ab "
				+ "FROM contiguity c "
				+ "WHERE c.year <= ? AND c.conttype = 1 "
				+ "ORDER BY id1";
		List<Country> result = new LinkedList <Country>();
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("id1");
				Country c = idMap.get(id);
				
				if(c!=null) {
					result.add(c);
				}else {
					System.out.println("Errore consistenza dati DB");
				}
			}
			
			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Border> getCountryPairs(int anno, Map<Integer, Country> idMap) {

		String sql = "SELECT state1no AS id1, state1ab, state2no AS id2, state2ab "
				+ "FROM contiguity c "
				+ "WHERE c.year <= ? AND c.conttype = 1";
		List <Border> result = new LinkedList<Border>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				int id1 = rs.getInt("id1");
				int id2 = rs.getInt("id2");
				Country c1 = idMap.get(id1);
				Country c2 = idMap.get(id2);
				
				if(c1!=null && c2!=null) {
					Border b = new Border(c1, c2);
					result.add(b);
				}else {
					System.out.println("Errore consistenza dati DB");
				}
			}
			
			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
}
