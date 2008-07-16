/* PropertizeTask.java - Getter/setter generating Ant task
 *
 * Copyright (c)2006 Ross Bamford & Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * File version: $Revision: 1.1 $ $Date: 2006/08/15 12:16:24 $
 * Originated: Aug 15, 2006
 * Author: Jeff Schnitzer (jeff<at>infohazard.org)
 */

package jen.tools.ant;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jen.SoftClass;
import jen.methods.BeanPropertyGetter;
import jen.methods.BeanPropertySetter;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

/**
 * This task accepts a fileset. It will examine any java class files included in
 * the fileset, looking for fields that have been annotated with the Property
 * annotation. For any such fields, public getter and setter methods will be
 * added to the class. If any getter or setter already exists, it is left alone.
 * 
 * The class files are rewritten in place.
 * 
 * @author Jeff Schnitzer
 */
public class PropertizeTask extends Task
{
  /** The magic number of a java class file */
  private static final int CLASS_MAGIC = 0xCAFEBABE;

  /** */
  List<FileSet> filesets = new ArrayList<FileSet>();

  /** */
  boolean verbose;

  /**
   * Want some extra logging output?
   */
  public void setVerbose(boolean value) {
    this.verbose = value;
  }

  /**
   * Called by ant for each nested fileset
   */
  public void addFileset(FileSet value) {
    this.filesets.add(value);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.tools.ant.Task#execute()
   */
  @Override
  public void execute() throws BuildException {
    try {
      Project p = getProject();

      for (FileSet fileset : this.filesets) {
        File dir = fileset.getDir(p);
        DirectoryScanner ds = fileset.getDirectoryScanner(p);

        for (String classFile : ds.getIncludedFiles()) {
          File absolute = new File(dir, classFile);

          if (absolute.isFile() && this.isClassFile(absolute))
            this.process(absolute);
        }
      }
    } catch (IOException ex) {
      throw new BuildException(ex, this.getLocation());
    } catch (ClassNotFoundException ex) {
      throw new BuildException(ex, this.getLocation());
    }
  }

  /**
   * Performs propertizing bytecode manipulation on the file, rewriting it in
   * place. Assumes the file exists and is a proper java classfile.
   */
  protected void process(File classFile) throws IOException,
      ClassNotFoundException {
    if (this.verbose) this.log("Checking " + classFile.toURL());

    InputStream in = new BufferedInputStream(new FileInputStream(classFile));
    SoftClass softie;
    try {
      ClassReader reader = new ClassReader(in);
      softie = new SoftClass(reader);
    } finally {
      in.close();
    }

    if (this.process(softie)) {
      this.log("Rewriting " + classFile.toURL());

      FileOutputStream fos = new FileOutputStream(classFile);
      try {
        fos.write(softie.generateBytecode());
      } finally {
        fos.close();
      }
    }
  }

  /**
   * Manipulates the soft class so that all appropriate methods are annotated.
   * 
   * @return true if the class changed, false if it did not require any
   *         manipulation.
   */
  protected boolean process(SoftClass softClazz) throws IOException,
      ClassNotFoundException {
    boolean changed = false;

    ClassNode node = softClazz.generateNode();
    for (Object f : node.fields) {
      FieldNode fNode = (FieldNode) f;
      if (fNode.visibleAnnotations != null) {
        for (Object ann : fNode.visibleAnnotations) {
          AnnotationNode aNode = (AnnotationNode) ann;
          AnnotationWrapper wrapper = new AnnotationWrapper(aNode);

          if (wrapper.isClass(Property.class)) {
            Boolean get = (Boolean) wrapper.getValue("get");
            if (get == null) get = true; // The default

            if (get) {
              BeanPropertyGetter getter = new BeanPropertyGetter(softClazz,
                  Opcodes.ACC_PUBLIC, fNode.name, fNode.name, 
                      Type.getType(fNode.desc));
              if (softClazz.getSoftMethod(getter.getName(), getter
                  .getDescriptor()) == null) {
                softClazz.putSoftMethod(getter);
                changed = true;
              }
            }

            Boolean set = (Boolean) wrapper.getValue("set");
            if (set == null) set = true; // The default

            if (set) {
              BeanPropertySetter setter = new BeanPropertySetter(softClazz,
                  Opcodes.ACC_PUBLIC, fNode.name, fNode.name, 
                      Type.getType(fNode.desc));
              if (softClazz.getSoftMethod(setter.getName(), setter
                  .getDescriptor()) == null) {
                softClazz.putSoftMethod(setter);
                changed = true;
              }
            }
          }
        }
      }
    }

    return changed;
  }

  /**
   * Is it a java classfile?
   */
  protected boolean isClassFile(File file) throws IOException {
    return this.checkMagic(file, CLASS_MAGIC);
  }

  /**
   * Checks to see if the file starts with the magic number.
   */
  protected boolean checkMagic(File file, long magic) throws IOException {
    DataInputStream in = new DataInputStream(new FileInputStream(file));
    try {
      int m = in.readInt();
      return magic == m;
    } finally {
      in.close();
    }
  }

  /**
   * Puts a sane interface on an AnntationNode.
   */
  public static class AnnotationWrapper
  {
    String className;

    Map<String, Object> values = new HashMap<String, Object>();

    public AnnotationWrapper(AnnotationNode node) {
      // Descriptors look like: Lcom.whatever.TheAnnotation;
      this.className = Type.getType(node.desc).getClassName();

      // Values are a List of pairs... String, then actual value of some random
      // type
      if (node.values != null) {
        Iterator it = node.values.iterator();
        while (it.hasNext()) {
          String name = (String) it.next();
          Object value = it.next();

          this.values.put(name, value);
        }
      }
    }

    public boolean isClass(Class clazz) {
      return clazz.getName().equals(this.className);
    }

    public Object getValue(String name) {
      return this.values.get(name);
    }
  }

}