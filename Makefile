BCC      = blitzcc
CFLAGS   = 
MAINFILE = main.bb
EXENAME  = DGX9142.exe
RELEASENAME = DGX9142_011
RELEASEDIR = ../release/$(RELEASENAME)

run: $(MAINFILE)
	$(BCC) $(CFLAGS) $(MAINFILE)

runquick: $(MAINFILE)
	$(BCC) $(CFLAGS) $(MAINFILE) -quickstart

debugrun: $(MAINFILE)
	$(BCC) $(CFLAGS) $(MAINFILE) -debug

debugfullrun: $(MAINFILE)
	$(BCC) $(CFLAGS) -d $(MAINFILE) -debug

build: $(MAINFILE)
	$(BCC) $(CFLAGS) -o $(EXENAME) $(MAINFILE)

release:
	make build
	rm -rf $(RELEASEDIR)
	mkdir -p $(RELEASEDIR)
	rsync -rE --exclude-from=ExcludeFromRelease DATA GFX MAPS SFX $(RELEASEDIR) && \
	cp $(EXENAME) changelog.txt README.md $(RELEASEDIR) && \
	mkdir $(RELEASEDIR)/SCREENS
	mv -f $(RELEASEDIR)/DATA/settings.ini.default $(RELEASEDIR)/DATA/settings.ini
