package types;

import parser.PascalSyntax;

public class ArrayType extends Type {
	public Type elemType, indexType;
	public int loLim, hiLim;

	public ArrayType(Type e, Type i, int lo, int hi) {
		elemType = e;  
		indexType = i;  loLim = lo;  hiLim = hi;
	}

	@Override public String identify() {
		return "type array [" + loLim + ".." + hiLim + ": " +
		indexType.identify() + "] of " + elemType.identify();
	}

	@Override public void checkType(Type tx, String op, 
		PascalSyntax where, String message) {
		if (tx instanceof ArrayType) {
			ArrayType txa = (ArrayType)tx;
			indexType.checkType(txa.indexType, "array index", where, message);
			elemType.checkType(txa.elemType, op, where, message);
		} else {
			System.out.println("ArrayType");
			where.error(message);
		}
	}


	@Override public int size() {
		return (hiLim-loLim+1)*elemType.size();
	}

	public Type typeOf(){
		return elemType;
	}

}
