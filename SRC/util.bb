;Util.bb ist meine pers�nliche Minilib mit einigen Funktionen, die man eigentlich in jedem Projekt
;braucht.
;Pr�fix ist Util_, jedoch gibt es hier einige Variablen ohne dieses Pr�fix. 

Const sith# = Float(1.00/16.00)
Const sith2#= sith#/16.00
Global sqr2# = Sqr(2.00)

Global paras$[1]	; Notwendig f�r Util_GetParas

Global fps_frames,fps_ms=MilliSecs(),fps_anzeige#,util_fpsupdate ; Die FPS...

Global racenames$[2]
racenames[0] = "Evianisch"
racenames[1] = "Ythearnisch"
racenames[2] = "Xatarianisch"

Global main_gspe#,util_lastmsc,main_mscleft# ; f�r den Timer...

Dim CRCTable( 255 )
Util_InitCRC32()

Type TCheckSum
	Field name$
	Field sum
End Type


;String & File ####################################################

Function Util_GetParas(txt$,trenn$="=")	; Diese Function sucht nach einem Zeichen in einem String und
	tpos = Instr(txt$,"##")				; unterteilt ihn an diesem in zwei Teile.
	If tpos<>0 Then txt$ = Left(txt$,tpos-1)
	 
	tpos = Instr(txt$,trenn$)
	If tpos>0
		paras[0] = Trim(Left(txt$,tpos-1))
		paras[1] = Trim(Right(txt$,Len(txt$)-tpos))
		
		paras[1] = Replace(paras[1],"g~",Left(gfxd$,3))
		paras[1] = Replace(paras[1],"d~",Left(datad$,4)) 
		paras[1] = Replace(paras[1],"s~",Left(sfxd$,3))
		;DebugLog "Para1: "+paras[0]+"  Para2: "+paras[1]
		Return 1
	Else
		paras[0] = txt$
	EndIf
End Function

Function util_trim2$(txt$)
	Local stringmode = 0
	For i = 1 To Len(txt)
		c$ =  Mid(txt,i,1)
		If stringmode
			If c = Chr(34) Then
				stringmode = 0
			Else
				rettxt$ = rettxt$ + c
			EndIf 
		Else
			Select c
			Case " ", Chr(9)
				; leerzeichen und tabs werden ignoriert
			Case "#"
				If Mid(txt,i,2) = "##" Then Exit Else rettxt$ = rettxt$ + c
			Case Chr(34)
				stringmode = 1
			Default
				rettxt$ = rettxt$ + c
			End Select
		EndIf
	Next
	Return rettxt$
End Function

Function Util_StripPath$(file$) 
   If Len(file$)>0 
      For i=Len(file$) To 1 Step -1 
         mi$=Mid$(file$,i,1) 
         If mi$="\" Or mi$="/" Then Return name$ Else name$=mi$+name$ 
      Next 
   EndIf 
   Return name$ 
End Function

Function Util_StripType$(file$) 
   If Len(file$)>0 
      For i=Len(file$) To 1 Step -1 
         mi$=Mid$(file$,i,1) 
         If mi$="." Then Return name$ Else name$=mi$+name$ 
      Next 
   EndIf 
   Return name$ 
End Function

; Diese Function prueft ein File auf Existenz. Incl. Error ;)
Function Util_CheckFile(path$,check=1)
	If FileType(path$)<>1 Then RuntimeError "Die Datei "+path$+" konnte nicht gefunden werden!"
	If check Then
		c.TCheckSum = New TCheckSum
		c\name	= path
		c\sum	= Util_CheckSum(path)
	EndIf
End Function

Function Util_CheckSum(filename$)
	; deactivated check sum computation, as files are not consistent between builds from linux git folders and windows git folders... 
	; the check sums were originally used to ensure consistency between game/map versions in multiplayer mode.
   Return 1
   
   ; check
   bank = CreateBank (FileSize(filename$))
   file = ReadFile (filename$)
   ReadBytes (bank, file, 0, FileSize(filename$))
   CloseFile file
   
   length% = BankSize (bank)
   hash% = -2
   
   For i% = 1 To length-1
      byte = PeekByte (bank, i)
      hash = CRCTable( ( hash Xor byte ) And 255 ) Xor ( hash Shr 8 )
   Next
   
   hash = ( hash Xor -2 )
   
   Return hash
End Function

Function Util_ClearCheckSums()
	Delete Each TCheckSum
End Function

Function Util_InitCRC32()
        poly% = 3988292384
       
        For i% = 0 To 255
                crc% = i
               
                For j% = 0 To 7
                        If ( crc And 1 )
                                crc = ( crc Shr 1 ) Xor poly
                        Else
                                crc = crc Shr 1
                        EndIf
                Next
               
                CRCTable( i ) = crc
        Next
End Function

Function Util_MakeURL$(txt0$)
	txt$ = ""
	For i = 1 To Len(txt0)
		char = Asc(Mid(txt0,i,1))
		If char<48 Or (char>57 And char<65) Or (char>90 And char<97) Or char>122 Then 
			txt$ = txt$ + "%"+Right(Hex(char),2)
		Else
			txt$ = txt$ + Chr(char)
		EndIf
	Next
	Return txt
End Function

Function Util_Shorten$(txt$,l)
	If Len(txt$) > l
		txt$ = Left(txt$,l-2)+".."
	EndIf
	Return txt
End Function

;Mathematisch ####################################################

Function Util_PytHyp#(kath1#,kath2#)
	Return Sqr(kath1^2+kath2^2)
End Function

Function Util_CoordinateDistance#(x1#,y1#,z1#,x2#,y2#,z2#) 
	Return Sqr(((x1-x2)^2)+((y1-y2)^2)+((z1-z2)^2))
End Function

Function Util_VectorLength#(x1#,y1#,z1#)
	Return Sqr(x1*x1+y1*y1+z1*z1)
End Function

Function util_minmax#(value#,min#,max#)
	If value < min Then value = min
	If value > max Then value = max
	Return value
End Function

;Timer und FPS ####################################################

Function Util_Fps()
	fps_frames = fps_frames+1
	If MilliSecs()-fps_ms >= 1000 Then
		fps_anzeige = fps_frames
		fps_frames	= 0
		fps_ms		= MilliSecs()
		util_fpsupdate = 1
	Else 
		util_fpsupdate = 0
	EndIf
	Return fps_anzeige
End Function

Function Util_InitTimer()
	util_lastmsc = MilliSecs()
End Function

Function Util_Timer()
	msc = MilliSecs()
	main_mscleft = Float#(msc-util_lastmsc)
	main_gspe# = main_mscleft/13.00
	If main_gspe > 100 Then main_gspe = 0
	util_lastmsc = msc
End Function


;Grafisch ####################################################

Function Util_Screenshot(pfad$,break=400)
	Repeat
		i = i + 1
		If FileType(pfad$+i+".bmp") = 0
			SaveBuffer BackBuffer(),pfad$+i+".bmp"
			Return 1
		EndIf
		If i >= break Then Return 0
	Forever
End Function

Function Util_GetScaleX#(entity)
	Return GetMatElement(entity,1,0)
End Function 

Function Util_GetScaleY#(entity)
	Return GetMatElement(entity,1,1)
End Function 

Function Util_GetScaleZ#(entity)
	Return GetMatElement(entity,1,2)
End Function 

Function Util_lightmapit(mesh,nx#,ny#,nz#)
	For s = 1 To CountSurfaces(mesh)
		surface = GetSurface(mesh,s)		
		For i = 0 To  CountTriangles(surface)-1
			For i2 = 0 To 2
				vert1 = TriangleVertex(surface,i,i2) 
				farbe1 = 255-Util_CoordinateDistance(nx#,ny#,nz#,VertexNX(surface,vert1),VertexNY(surface,vert1),VertexNZ(surface,vert1))*140
				VertexColor(surface,vert1, farbe1, farbe1,farbe1) 
			Next
		Next
	Next 
End Function

Function Util_DrawRectSprite(sprite,x,y,width,height,border=0)
	PositionEntity sprite,x-main_hwidth,main_hheight-y,main_hwidth
	ScaleSprite sprite,width/2,height/2
	If border
		Color gui_colr,gui_colg,gui_colb
		If width<0 Or height<0
			Line x,y,x+width,y
			Line x,y,x,y+height
			Line x+width,y,x+width,y+height
			Line x,y+height,x+width,y+height
		Else
			Rect x,y,width,height,0
		EndIf
	EndIf
End Function

Function Util_Approach(mesh,x#,y#,z#,f#=1)
	f = 1.0 - (1.0 - f)^main_gspe; f ^ (1.0/main_gspe#)
	f = Util_MinMax(f,0,1)
	TranslateEntity mesh,-(EntityX(mesh)-x)*f,-(EntityY(mesh)-y)*f,-(EntityZ(mesh)-z)*f
End Function

;Bankzeugs ####################################################

Function Util_PokeSting(bank,pos,txt$)
	For i = 1 To Len(txt)
		PokeByte bank,pos + i - 1, Asc(Mid(txt,i,1))
	Next
End Function

Function Util_PeekString$(bank,start,Ende)
	For i = start To ende
		txt$ = txt$ + Chr(PeekByte(bank,i))
	Next
	Return txt$
End Function

Function Util_BankToString$(bank)
	Return Util_PeekString(bank,0,BankSize(bank)-1)
End Function

Function Util_FindNext(bank,pos,byte)
	size = BankSize(bank)-1
	For i = pos+1 To size
		If PeekByte(bank,i) = byte Then Return i
	Next
End Function

Function Util_FileToBank(path$)
	size = FileSize(path)
	bank = CreateBank(size+1)
	file = ReadFile(path)
		
	For i = 0 To size-1
		PokeByte bank,i,ReadByte(file)
	Next
	PokeByte bank,size,3
	
	CloseFile file
	Return bank
End Function

Function Util_BankToFile(bank,path$)
	file = WriteFile(path)
		
	For i = 0 To BankSize(bank)-1
		WriteByte file, PeekByte(bank,i)
	Next
	
	CloseFile file
End Function

;David Woodgates Code zum automatischen definieren von EntityBox

Const Infinity# = (999.0)^(99999.0), NaN# = (-1.0)^(0.5)

Global Mesh_MinX#,Mesh_MinY#,Mesh_MinZ#
Global Mesh_MaxX#,Mesh_MaxY#,Mesh_MaxZ#
Global Mesh_MagX#,Mesh_MagY#,Mesh_MagZ#

; create a collision box for a mesh entity taking into account entity scale
; (will not work in non-uniform scaled space)
Function makecollbox(mesh)
	Local sx# = EntityScaleX(mesh,1)
	Local sy# = EntityScaleY(mesh,1)
	Local sz# = EntityScaleZ(mesh,1)
	GetMeshExtents(mesh)
	EntityBox mesh, Mesh_MinX*sx,Mesh_MinY*sy,Mesh_MinZ*sz,Mesh_MagX*sx,Mesh_MagY*sy,Mesh_MagZ*sz
End Function

; make a proxy bounding box for a mesh
; (will work in non-uniform scaled space using sphere to poly collisions)
Function makeproxybox(mesh,alpha#=0)
	Local proxy = CreateCube(mesh)
	EntityAlpha proxy,alpha
	GetMeshExtents(mesh)
	FitMesh proxy, Mesh_MinX,Mesh_MinY,Mesh_MinZ, Mesh_MagX,Mesh_MagY,Mesh_MagZ
    Return proxy
End Function

; Find Mesh extents
Function GetMeshExtents(Mesh)
	Local s,surf,surfs,v,verts,x#,y#,z#
	Local minx# = Infinity
	Local miny# = Infinity
	Local minz# = Infinity
	Local maxx# = -Infinity
	Local maxy# = -Infinity
	Local maxz# = -Infinity

	surfs = CountSurfaces(mesh)

	For s=1 To surfs
	
		surf  = GetSurface(mesh,s)
		verts = CountVertices(surf)
		
		For v=0 To verts-1
				
			x = VertexX(surf,v)
			y = VertexY(surf,v)
			z = VertexZ(surf,v)
			
			If x < minx Then minx = x Else If x > maxx Then maxx = x
			If y < miny Then miny = y Else If y > maxy Then maxy = y
			If z < minz Then minz = z Else If z > maxz Then maxz = z
			
		Next

	Next
	
	Mesh_MinX = minx
	Mesh_MinY = miny
	Mesh_MinZ = minz
	Mesh_MaxX = maxx
	Mesh_MaxY = maxy
	Mesh_MaxZ = maxz
	Mesh_MagX = maxx-minx
	Mesh_MagY = maxy-miny
	Mesh_MagZ = maxz-minz

End Function

Function EntityScaleX#(entity, globl=False) 
	If globl Then TFormVector 1,0,0,entity,0 Else TFormVector 1,0,0,entity,GetParent(entity) 
	Return Sqr(TFormedX()*TFormedX()+TFormedY()*TFormedY()+TFormedZ()*TFormedZ()) 
End Function 

Function EntityScaleY#(entity, globl=False)
	If globl Then TFormVector 0,1,0,entity,0 Else TFormVector 0,1,0,entity,GetParent(entity)  
	Return Sqr(TFormedX()*TFormedX()+TFormedY()*TFormedY()+TFormedZ()*TFormedZ()) 
End Function 

Function EntityScaleZ#(entity, globl=False)
	If globl Then TFormVector 0,0,1,entity,0 Else TFormVector 0,0,1,entity,GetParent(entity)  
	Return Sqr(TFormedX()*TFormedX()+TFormedY()*TFormedY()+TFormedZ()*TFormedZ()) 
End Function

Function EntityDistanceB#(entity1, entity2, radius# = 10000)
	dx# = EntityX(entity1,1)-EntityX(entity2,1)
	dy# = EntityY(entity1,1)-EntityY(entity2,1)
	dz# = EntityZ(entity1,1)-EntityZ(entity2,1)
	
	If Abs(dx)<radius And Abs(dy)<radius And Abs(dz)<radius Then
		radius = Sqr(dx*dx+dy*dy+dz*dz)
	EndIf
	
	Return radius
End Function

Function EntityInBoxDistance#(entity1, entity2, radius# = 10000)
	dx# = EntityX(entity1,1)-EntityX(entity2,1)
	dy# = EntityY(entity1,1)-EntityY(entity2,1)
	dz# = EntityZ(entity1,1)-EntityZ(entity2,1)
	
	If Abs(dx)<radius And Abs(dy)<radius And Abs(dz)<radius Then
		Return True
	EndIf
	
	Return False
End Function

Function Util_CreateSprite(parent)
	mesh		= CreateMesh(parent)
	sur			= CreateSurface(mesh)
	
	h=1
	w=1
	
	Local vert[3]
	vert[0]	= AddVertex(sur,	-w,-h,0,		0,1)
	vert[1]	= AddVertex(sur,	w,-h,0,		1,1)
	vert[2]	= AddVertex(sur,	-w,h,0,			0,0)
	vert[3]	= AddVertex(sur,	w,h,0,		1,0)
	
	AddTriangle sur, vert[0], vert[2], vert[1]
	AddTriangle sur, vert[1], vert[2], vert[3]
	
	EntityFX mesh,1+8+16
	
	Return mesh
End Function

Function Util_LoadSprite(texfile$, flag, parent=0)
	sprite = Util_CreateSprite(parent)
	tex = LoadTexture(texfile,flag)
	
	EntityTexture sprite, tex
	
	Return sprite
End Function