package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.Dom4JDriver;

import model.ScoreInfos;

public class SalvarScoreInfos{
	
	private ArrayList<ScoreInfos> scoreInfos;

	private XStream xStream;
	private File file;
	

	public SalvarScoreInfos(String type, String endress) {
		
		String userHomeFolder = System.getProperty("user.home") + "/Desktop";

		xStream = new XStream(new Dom4JDriver());
		xStream.alias(type, ScoreInfos.class);
		
		file = new File(userHomeFolder, endress);
		scoreInfos = new ArrayList<>();
		
	}

	public void salvarScore(ScoreInfos scoreinfos) {

		try {
			if (!file.exists()){
				scoreInfos.add(scoreinfos);
				file.createNewFile();
			}
			else {
				scoreInfos.add(scoreinfos);
				scoreInfos.addAll(lerScores());
				file.delete();
				file.createNewFile();
			}
			
			OutputStream stream = new FileOutputStream(file);
			xStream.toXML(scoreInfos, stream);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public List<ScoreInfos> lerScores()
	{
		if (!file.exists()){
			return null;
		}
		else {
			return (List<ScoreInfos>) xStream.fromXML(file);				
		}
		
	}
	
}
