package inc.glamdring.bitecode.classfile.structure;

import java.lang.reflect.*;
import java.nio.*;

enum TableRecord {

    ConstantPoolRecord(  inc.glamdring.bitecode.classfile.structure.ConstantPoolRecord.recordLen * 0x10000),
    interfaces(0x40000),
    fields(0x40000),
    methods(0x40000),
    attributes(0x40000);
    public static int recordLen;
    public int size, seek;

    TableRecord(int size) {
        this.size = size;
        init();
    }

    void init() {
        seek = recordLen;
        recordLen += size;
    }
}
//@@ #endTableRecord