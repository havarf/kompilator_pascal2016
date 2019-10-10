package types;

import main.*;
import parser.PascalSyntax;

public abstract class Type {
    public abstract String identify();

    public void checkType(Type tx, String op, PascalSyntax where, String mess) {
	Main.log.noteTypeCheck(this, op, tx, where);
	if (this != tx)
	    where.error(mess);
    }

    public abstract int size();

    public abstract Type typeOf();
}
