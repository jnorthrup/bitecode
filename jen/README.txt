JEN - HIGH-LEVEL CLASS GENERATION / TRANSFORMATION API FOR JAVA(TM)
Release notes and readme information

*******************************************************************************
** Jen is released  as OPEN  SOURCE under the  terms of the APACHE  SOFTWARE **   
** LICENSE, 2.0, as detailed in the enclosed LICENSE.txt. You may use, copy, **
** modify, and  redistribute this software strictly in  accordance  with the **
** terms set out therein.                                                    **
**                                                                           **
**    Copyright  (c) 2005  Roscopeco  Open  Technologies  &  Contributors.   ** 
** All trademarks and registered trademarks mentioned remain the property of ** 
**             their original owners. All other rights reserved.             **  
*******************************************************************************

CONTENTS
--------

  + Welcome to Jen
  + Installation
  + Now what?
  + Contact Information
  

WELCOME TO JEN
--------------

  This section is under construction. Please see the HTML documentation
  bundled with binary downloads, or available at:
  
  	http://jen.dev.java.net/nonav/docs/index.html    	
  

INSTALLATION
------------

  If you have downloaded from source, the first thing you'll need to do is
  build the project, using Maven. Simply issuing the command 'maven' from
  the top-level directory will build all the jars, while 'maven all' will
  also build the documentation.
  
  The compiled source (or just binary) distribution consists of three jars,
  'jen-core-VERSION.jar' and 'jen-members-VERSION.jar', which separate the
  core SoftClass API from the standard SoftMember implementations, and
  'jen-VERSION.jar', which contains the full library. Which you choose 
  depends entirely on your usage.
  
  There is no need to install the binary version into your repository manually,
  since we publish artifacts in the main Java.net Maven repository. To use
  this, simply add the following (for example) to your project.properties:
  
  		maven.repo.remote=http://www.ibiblio.org/maven/,
  		       https://maven-repository.dev.java.net/repository
  		       
  Make sure this is all on one line, and then add Dependency tags appropriately
  to your project.xml.
  
  If you want to install the binary distribution to your Maven repository
  simply copy all three jars to the appropriate place (by default, it's
  something like ~/.maven/repository/jen/jars). This is handled automatically
  by the source distribution as part of the build.
          

NOW WHAT?
---------

  This section is under construction. 
  
  Also see the wiki at http://wiki.java.net/bin/view/Javatools/JenSoftClass


CONTACT INFORMATION
-------------------

  The Project Homepage : http://jen.dev.java.net/
  Current documentation: http://jen.dev.java.net/nonav/docs/index.html
  The Jen Wiki         : http://wiki.java.net/bin/view/Javatools/JenSoftClass
    
  ASM                  : http://asm.objectweb.org/
  
  Roscopeco Open Technologies are commited to the principals of Professional 
  Open Source, and believe in OSS not just as a way to gain kudos and keep
  hackers happy, but as a real, viable, attractive alternative to commercially
  licenced software. We believe that by maintaining a firm commitment to 
  "the full system, free, for everyone" and to open community development we 
  can continue to produce the highest quality software with the widest possible
  audience. Jen is, and will remain, OSI Approved Open Source, and we are keen
  to hear from anyone interested in contributing, whether it's with ideas, 
  code, documentation, or whatever!

  Please remember that this is free software, provided in the hope that it
  will be useful, but with no warranties. If you are having problems using
  or extending Jen, please get in touch with us via the mailing list:
  
  	users@jen.dev.java.net
  	
  A full list of mailing lists (together with archives and subscribe links) for
  the project can be found at:
  
  	http://jen.dev.java.net/nonav/docs/mail-lists.html
    
  'Java' is a trademark of Sun Microsystems. All trademarks are recognised
  as the property of their respective owners. 'ASM' is separately-licenced
  Open Source software, and remains the copyright property of Objectweb and 
  it's contributors. See the ASM site for licence information.
