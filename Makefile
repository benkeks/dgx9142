BCC      = blitzcc
CFLAGS   = 
MAINFILE = main.bb
EXENAME  = DGX9142.exe
FMODDLL  = ~/.wine/drive_c/Program\ Files/Blitz3d/bin/fmod.dll
RELEASENAME = dgx9142
RELEASEVERSION  = 0.2.1
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
	cp $(FMODDLL) $(RELEASEDIR) && \
	mkdir $(RELEASEDIR)/SCREENS
	
releaseitch:
	make release
	butler push $(RELEASEDIR) benkeks/dgx9142:win32 --userversion $(RELEASEVERSION)

