CC      = ~/Software/Blitz3d/blitzcc.sh
CFLAGS  = 
MAINFILE = main.bb
EXENAME = DGX9142.exe
RELEASENAME = DGX9142
RELEASEDIR = ../release/$(RELEASENAME)

run: $(MAINFILE)
	$(CC) $(CFLAGS) $(MAINFILE)

runquick: $(MAINFILE)
	$(CC) $(CFLAGS) $(MAINFILE) -quickstart

debugrun: $(MAINFILE)
	$(CC) $(CFLAGS) -d $(MAINFILE) -debug

build: $(MAINFILE)
	$(CC) $(CFLAGS) -o $(EXENAME) $(MAINFILE)

release:
	make build
	rm -rf $(RELEASEDIR)
	mkdir -p $(RELEASEDIR)
	rsync -rE --exclude-from=ExcludeFromRelease DATA GFX MAPS SFX $(RELEASEDIR) && \
	cp $(EXENAME) changelog.txt README.md $(RELEASEDIR) && \
	mkdir $(RELEASEDIR)/SCREENS
	mv -f $(RELEASEDIR)/DATA/settings.ini.default $(RELEASEDIR)/DATA/settings.ini