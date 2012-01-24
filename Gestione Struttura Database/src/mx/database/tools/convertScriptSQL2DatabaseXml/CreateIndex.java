/**
 * 
 */
package mx.database.tools.convertScriptSQL2DatabaseXml;

import java.util.Vector;

import mx.database.struttura.exception.DatabaseException;
import mx.database.struttura.xsd.Database;
import mx.database.struttura.xsd.Table;

/**
 * @author massi
 *
 */
public class CreateIndex
{

	/**
	 * 
	 */
	public CreateIndex()
	{
	}

	public static void convert(Database database, Vector<String> lines) throws DatabaseException
	{
		CreateIndex createIndex = null;
		createIndex = new CreateIndex();
		createIndex.esegui(database, lines);
	}

	public void esegui(Database database, Vector<String> lines) throws DatabaseException
	{
		Table table = null;
		String columns = null;
		
		try
		{
			if (lines.get(0).startsWith("create unique index"))
				table = AlterTable.findTable(database, lines.get(0).split(" ")[5]);
			else
				table = AlterTable.findTable(database, lines.get(0).split(" ")[4]);
			columns = "";
			for (int x=2; x<lines.size()-1; x++)
				columns += (columns.equals("")?"":", ")+lines.get(x);

			if (lines.get(0).startsWith("create unique index"))
				table.getIndex().add(AlterTable.genIndex(lines.get(0).split(" ")[3], false, columns));
			else
				table.getIndex().add(AlterTable.genIndex(lines.get(0).split(" ")[2], false, columns));
		}
		catch (DatabaseException e)
		{
			throw new DatabaseException("Procedura CreateIndex: "+e.getMessage());
		}
	}
}
