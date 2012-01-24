/**
 * 
 */
package mx.database.tools.convertScriptSQL2DatabaseXml;

import java.math.BigInteger;
import java.util.Vector;

import mx.database.struttura.xsd.Column;
import mx.database.struttura.xsd.Database;
import mx.database.struttura.xsd.Table;

/**
 * @author massi
 *
 */
public class CreateTable
{

	/**
	 * 
	 */
	public CreateTable()
	{
	}

	public static void convert(Database database, Vector<String> lines)
	{
		CreateTable createTable = null;
		createTable = new CreateTable();
		createTable.esegui(database, lines);
	}

	public void esegui(Database database, Vector<String> lines)
	{
		Table table = null;

		table = new Table();
		table.setName(lines.get(0).replace("create table ", ""));

		for (int x=2; x<lines.size()-1; x++)
		{
			table.getColumn().add(genColum(lines.get(x)));
		}
		database.getTable().add(table);
	}

	private Column genColum(String line)
	{
		Column column = null;
		String dimensione = null;
		int pos = 0;

		column = new Column();

		pos = line.indexOf(" ");
		column.setValue(line.substring(0,pos));
		line = line.substring(pos).trim();

		pos = line.indexOf(" ");
		if (pos > -1)
		{
			dimensione = line.substring(0,pos);
			line = line.substring(pos).trim();
		}
		else
		{
			pos = line.indexOf(",");
			if (pos > -1)
			{
				dimensione = line.substring(0,pos);
				line = line.substring(pos).trim();
			}
			else
			{
				dimensione = line;
				line = "";
			}
		}

		if (dimensione.equals("int(11)"))
			column.setType("INT11");
		else if (dimensione.equals("int(4)"))
			column.setType("INT4");
		else if (dimensione.startsWith("varchar"))
		{
			column.setType("VARCHAR");
			column.setLength(new BigInteger(dimensione.substring(8, dimensione.length()-1)));
		}
		else if (dimensione.startsWith("char"))
		{
			column.setType("CHAR");
			column.setLength(new BigInteger(dimensione.substring(5, dimensione.length()-1)));
		}
		else if (dimensione.equals("timestamp"))
			column.setType("TIMESTAMP");
		else if (dimensione.equals("time"))
			column.setType("TIME");
		else
			System.out.println("Tipologia dimensione non riconosciuta ["+dimensione+"]");
		
		if (line.indexOf("not null")>-1)
			column.setNull(false);
		else
			column.setNull(true);
		return column;
	}
}
