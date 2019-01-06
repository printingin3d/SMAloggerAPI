package eu.printingin3d.smalogger.api.smajava;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TagDefs {
	private static final Logger LOGGER = LoggerFactory.getLogger(TagDefs.class);
	
	final static int READ_OK = 0;
	final static int READ_ERROR = -1;
	private static TagDefs instance;
	
	private class TD
	{
		private String m_tag; 	//label
		private int m_lri; 		//Logical Record Index
		private String m_desc; 	//Description
		
		public TD(String tag, int lri, String desc)
		{
			this.m_tag = tag;
			this.m_lri = lri;
			this.m_desc = desc;
		}
		
		public String getTag() 
		{ 
			return m_tag; 
		}
		
		public int getLRI()
		{ 
			return m_lri; 
		}
		
		public String getDesc()
		{ 
			return m_desc; 
		}
	}
	
	private Map<Integer, TD> m_tagdefmap = new HashMap<>();
	
	private void print_error(String msg, int line, String fpath)
	{
		LOGGER.error(msg + " on line " + line + " [" + fpath + "]\n");
	}
	
	private void add(int tagID, String tag, int lri, String desc)
	{
		m_tagdefmap.put(tagID, new TD(tag, lri, desc));
	}
	
	private TagDefs()
	{
		
	}
	
	public static TagDefs getInstance()
	{
		if(instance == null)
		{
			instance = new TagDefs();
		}
		return instance;
	}
	
	public void readall(String locale) throws IOException {
		locale = locale.toUpperCase();

		//Build fullpath to taglist<locale>.txt
		//Default to EN-US if localized file not found
		String fn_taglist = "/TagList" + locale + ".txt";

		InputStream in = getClass().getResourceAsStream(fn_taglist);
		
		try (Reader fr = new InputStreamReader(in);
				BufferedReader br = new BufferedReader(fr)) {
			String line;
			int lineCnt = 0;
			while((line = br.readLine()) != null)
			{
				lineCnt++;
				
				//Get rid of comments and empty lines
				int hashpos = -1;
				if(line.startsWith("#") || line.startsWith("\r"))
				{
					hashpos = line.indexOf('#');
				}		
				if(hashpos == -1) {
					hashpos = line.indexOf('\r');
				}
				
				if (hashpos != -1) {
					line = line.substring(0, hashpos);
				}
				
				if(line.length() > 0)
				{
					//Split line TagID=Tag\Lri\Descr
					String[] lineparts;
					lineparts = line.split("[=\\\\]");
					if (lineparts.length != 4)
					{
						print_error("Wrong number of items", lineCnt, fn_taglist);
					}
					else
					{
						int entryOK = 1;
						int tagID = 0;
						try
						{
							tagID = Integer.parseInt(lineparts[0]);
						}
						catch(NumberFormatException e)
						{
							print_error("Invalid tagID", lineCnt, fn_taglist);
							entryOK = 0;
						}

						int lri = 0;
						try
						{
							lri = Integer.parseInt(lineparts[2]);
						}
						catch(NumberFormatException e)
						{
							print_error("Invalid LRI", lineCnt, fn_taglist);
							entryOK = 0;
						}

						if (entryOK == 1)
						{
							String tag = lineparts[1];
							tag = tag.trim();

							String descr = lineparts[3];
							descr = descr.trim();

							add(tagID, tag, lri, descr);
						}
					}
				}
			}
		}
	}
	
	public String getTag(int tagID) 
	{ 
		return m_tagdefmap.get(tagID).getTag();
	}
	
	public int getTagIDForLRI(int LRI)
	{
		LRI &= 0x00FFFF00;
		for (Map.Entry<Integer, TD> entry : m_tagdefmap.entrySet())
		{
		    if (LRI == entry.getValue().getLRI()) {
				return entry.getKey();
			}
		}
		return 0;
	}

	public String getTagForLRI(int LRI)
	{
		LRI &= 0x00FFFF00;
		for (Map.Entry<Integer, TD> entry : m_tagdefmap.entrySet())
		{
			if (LRI == entry.getValue().getLRI()) {
				return entry.getValue().getTag();
			}
		}
		return "";
	}

	public String getDescForLRI(int LRI)
	{
		LRI &= 0x00FFFF00;
		for (Map.Entry<Integer, TD> entry : m_tagdefmap.entrySet())
		{
			if (LRI == entry.getValue().getLRI()) {
				return entry.getValue().getDesc();
			}
		}
		return "";
	}
	
	
	public int getLRI(int tagID) 
	{ 
		return m_tagdefmap.get(tagID).getLRI();
	}
	
	public String getDesc( int tagID) 
	{ 
		return m_tagdefmap.get(tagID).getDesc();
	}
	
	public String getDesc(int tagID, String _default) 
	{
		return (m_tagdefmap.get(tagID)==null || m_tagdefmap.get(tagID).getDesc() == null) ? _default : m_tagdefmap.get(tagID).getDesc(); 
	}
	
	public int size()
	{
		return m_tagdefmap.size();
	}
}
