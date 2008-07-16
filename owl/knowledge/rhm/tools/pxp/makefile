all :
	make -C lib
	make -C gui
	make -C xml
	make -C ivib
	make -C parser
	make -C unidep
	make -C unidoc
	make -C util

clean :
	make -C lib clean
	make -C gui clean
	make -C xml clean
	make -C ivib clean
	make -C parser clean
	make -C unidep clean
	make -C unidoc clean
	make -C util clean

windows : all
	cp */*.exe bin/
	cd bin; for f in *.exe; do ln -s $f ${f%.exe}; done
	cd bin; ln -s testxml pxp

setup:
	ln -s /home/unix/bin/make.exe bin/make
	ln -s /home/unix/bin/dirname bin/dirname
