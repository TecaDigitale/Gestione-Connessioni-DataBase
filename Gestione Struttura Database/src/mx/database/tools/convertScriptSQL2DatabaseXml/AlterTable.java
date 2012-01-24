/**
 * 
 */
package mx.database.tools.convertScriptSQL2DatabaseXml;

import java.util.Vector;

import mx.database.struttura.exception.DatabaseException;
import mx.database.struttura.xsd.Column;
import mx.database.struttura.xsd.Constraint;
import mx.database.struttura.xsd.Database;
import mx.database.struttura.xsd.Index;
import mx.database.struttura.xsd.Table;

/**
 * @author massi
 *
 */
public class AlterTable
{

	/**
	 * 
	 */
	public AlterTable()
	{
	}

	public static void convert(Database database, Vector<String> lines) throws DatabaseException
	{
		AlterTable alterTable = null;
		alterTable = new AlterTable();
		alterTable.esegui(database, lines);
	}

	public void esegui(Database database, Vector<String> lines) throws DatabaseException
	{
		Table table = null;
		String[] st = null;
		String line = null;
		
		try
		{
			if (lines.size()==1)
			{
				if (lines.get(0).indexOf("comment")>-1)
				{
					table = findTable(database, lines.get(0).split(" ")[2]);
					table.setComment(lines.get(0).substring(lines.get(0).indexOf("'")+1, lines.get(0).length()-2));
				}
			}
			else if (!lines.get(1).startsWith("drop"))
			{
				table = findTable(database, lines.get(0).split(" ")[2]);
				if (lines.get(1).startsWith("add primary key"))
				{
					st = lines.get(1).substring(lines.get(1).indexOf("(")+1, lines.get(1).length()-2).split(",");
					for (int x=0; x<st.length; x++)
					{
						for (int y=0; y<table.getColumn().size(); y++)
						{
							if (table.getColumn().get(y).getValue().equals(st[x].trim()))
								table.getColumn().get(y).setPrimaryKey(true);
						}
					}
				}
				else if (lines.get(1).startsWith("add unique"))
				{
					table.getIndex().add(genIndex(lines.get(1).split(" ")[2], true, lines.get(1).substring(lines.get(1).indexOf("(")+1, lines.get(1).length()-2)));
				}
				else if (lines.get(0).indexOf("constraint")>-1)
				{
					line = "";
					for (int x=0; x<lines.size(); x++)
						line += lines.get(x)+" ";
					line = line.trim();
					table.getConstraint().add(genConstraint(line));
				}
				else
				{
					System.out.println(lines.toString());
				}
			}
		}
		catch (DatabaseException e)
		{
			throw new DatabaseException("Procedura AlterTable: "+e.getMessage());
		}
	}

	private Constraint genConstraint(String line)
	{
		Constraint constraint = null;
		int pos = 0;
		
		constraint = new Constraint();

		pos = line.indexOf("constraint");
		line = line.substring(pos+11).trim();
		pos = line.indexOf(" ");
		constraint.setName(line.substring(0,pos).trim());

		line = line.substring(pos).trim();
		pos = line.indexOf("(");
		line = line.substring(pos+1).trim();
		pos = line.indexOf(")");
		constraint.setColumn(new Column());
		constraint.getColumn().setValue(line.substring(0,pos));

		line = line.substring(pos+1).trim();
		pos = line.indexOf(" ");
		line = line.substring(pos+1).trim();
		pos = line.indexOf(" ");
		constraint.setTable(new Table());
		constraint.getTable().setName(line.substring(0,pos));

		pos = line.indexOf("(");
		constraint.getTable().getColumn().add(new Column());
		constraint.getTable().getColumn().get(0).setValue(line.substring(pos+1, line.length()-2));
		return constraint;
	}

	public static Index genIndex(String idxName, boolean unique, String columns)
	{
		Index index = null;
		Column column = null;
		String[] st = null;

		index = new Index();
		index.setName(idxName);
		index.setUnique(unique);

		st = columns.split(",");
		for (int x=0; x<st.length; x++)
		{
			if (st[x].trim().indexOf(" ")> -1)
			{
				System.out.println("genIndex: "+st[x]);
			}
			else
			{
				column = new Column();
				column.setValue(st[x].trim());
				column.setCrescent(true);
				index.getColumn().add(column);
			}
		}
		return index;
	}

	public static Table findTable(Database database, String tableName) throws DatabaseException
	{
		Table table = null;

		for (int x=0; x<database.getTable().size(); x++)
		{
			if (database.getTable().get(x).getName().equals(tableName))
				table = database.getTable().get(x);
		}
		if (table == null)
			throw new DatabaseException("La tabella ["+tableName+"] non esiste");
		return table;
	}
}
