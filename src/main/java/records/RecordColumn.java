package records;

import javafx.scene.control.TableColumn;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.Map;


/**
 * An extension of a generic table column that has a 
 * refernece to the type of colume it will be in genarating a record
 * 
 * A REAL is a feature column that gets treated as a real value (float, int, double) 
 * A CATEGORY is a string value that gets binarized
 * A TIME is a date/timestamp
 * 
 * A real_label will be treated as any value and binarized into a class (0 = min value, N = max_value)
 * A class_label is any integer class (0, 1, 2...) 
 * 
 * INFO is a column that's just meta or info data and will be ignored in creating a record
 * 
 * @author lisztian
 *
 */
public class RecordColumn extends TableColumn<Map, Object> {
	
	
	public enum Type {
	    REAL (0),
	    CATEGORY (1),
	    TIME (2),
	    REAL_LABEL (3),
	    CLASS_LABEL (4),
	    INFO (5); 
	
	    private final int levelCode;

	    Type(int levelCode) {
	        this.levelCode = levelCode;
	    }
	    
	    public int getLevelCode() {
	        return this.levelCode;
	    }
	    
	}
	
	private Type column_type;
	private HashMap<String, Integer> category_map;
	
	private String name;
	private int which_type = 1;
	private String meta;
	
	public RecordColumn(String _string) {
		
		super(_string);
		
		this.name = _string;
		
		if(_string.contains("cat_")) {
			column_type = Type.CATEGORY;
			which_type = 1;
		}
		else {
			column_type = Type.REAL;
			which_type = 0;
		}
	}
	
	public RecordColumn(String _string, boolean cat) {
		
		super(_string);
		
		this.name = _string;
		
		if(cat) {
			column_type = Type.CATEGORY;
			which_type = 1;
		}
		else {
			column_type = Type.REAL;
			which_type = 0;
		}
	}
	
	public Type getColumn_type() {
		return column_type;
	}
	public void setColumn_type(Type column_type) {
		this.column_type = column_type;
	}
	public String getMeta() {
		return meta;
	}
	public void setMeta(String meta) {
		this.meta = meta;
	}

	
	public int getWhich_type() {
		return which_type;
	}
	public void setWhich_type(int which_type) {
		this.which_type = which_type;
	}

	
	public void buildColumnFactory(Callback call) {		
		setCellFactory(call);		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public void setCategory_map(HashMap<String, Integer> category_map) {
		this.category_map = category_map;
		
	}
	
	public HashMap<String, Integer> getCategory_map() {
		return category_map;
	}
	
	
}
