package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.Dom4JDriver;

import model.GameInfos;

public class SalvarGameInfos{
	
	private ArrayList<GameInfos> gameinfos;

	private XStream xStream;
	private File file;
	

	public SalvarGameInfos(String type, String endress) {
		
		String userHomeFolder = System.getProperty("user.home") + "/Desktop";

		xStream = new XStream(new Dom4JDriver());
		xStream.alias(type, GameInfos.class);
		
		file = new File(userHomeFolder, endress);
		gameinfos = new ArrayList<>();
		
	}

	public void salvarGameInfos(GameInfos gameinfo) {
		
		gameinfos.add(gameinfo);

		try {
			if (!file.exists())
				file.createNewFile();
			else {
				file.delete();
				file.createNewFile();
			}
			
			OutputStream stream = new FileOutputStream(file);
			xStream.toXML(gameinfos, stream);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public List<GameInfos> lerGameInfos()
	{
		if (!file.exists()){
//			System.out.println("Arquivo nao existe ou nao encontrado");
			return null;
		}
		else {
//			System.out.println("Arquivo encontrado");
			return (List<GameInfos>) xStream.fromXML(file);				
		}
			
	}
	
}
