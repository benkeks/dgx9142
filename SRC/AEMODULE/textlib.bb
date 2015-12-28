Type txt_font
	Field tex
	Field width#,height#
	Field brush, brush2
	Field widths[94]
End Type


Const txt_width# = 1/94.0

Function Txt_LoadFont(fontp$,mode,width#,height#,inf$)
	f.txt_font	= New txt_font
	f\tex		= LoadTexture(fontp$,mode)
	f\width		= width
	f\height	= height
	
	stream = ReadFile(inf$)
	For i = 0 To 93
		f\widths[i] = ReadLine(stream)
	Next
	CloseFile stream
	
	f\brush = CreateBrush()
	BrushTexture f\brush, f\tex
	BrushFX f\brush, 1+8+16
	
	f\brush2 = CreateBrush()
	BrushFX f\brush2, 1+8+16
	
	Return f\tex
End Function

Function Txt_Text(txt$, font, par=0, align=0, maxwidth=10000)
	For f.txt_font = Each txt_font
		If f\tex = font Then Exit
	Next
	
	txt$ = Replace(txt$,"ä","ae")
	txt$ = Replace(txt$,"ß","ss")
	txt$ = Replace(txt$,"ö","oe")
	txt$ = Replace(txt$,"ü","ue")
	
	mesh = CreateMesh(par)
	sur = CreateSurface(mesh, f\brush)
	
	Local vert[3]
	
	x# = Len(txt) * f\width * align
	
	For i = 1 To Len(txt$)
		Ascii = Asc(Mid(txt$,i,1))
		
		Select ascii
		Case 9
			i = i + 1
			Ascii = Asc(Mid(txt$,i,1))-48
			x# = 22*ascii
		Case 13
			x# = 0
			y# = y-f\height
		Case 32
			vert[0] = AddVertex(sur, x#,y+f\height,0,			0,0) ; ol
			vert[1] = AddVertex(sur, x#,y,0,					0,0) ; ul
			vert[2] = AddVertex(sur, x#+f\width,y+f\height,0,	0,0) ; or
			vert[3] = AddVertex(sur, x#+f\width,y,0,			0,0) ; ur
			
			x# = x# + f\width
			AddTriangle(sur,vert[2],vert[1],vert[0])
			AddTriangle(sur,vert[1],vert[2],vert[3])
			If x > maxwidth Then
				x# = 0
				y# = y-f\height
			EndIf
		Case 35 ; #+befehl
			i0 = i
			comm$ = ""
			para$ = ""
			While i < Len(txt$)
				i = i + 1
				char$ = Mid(txt$,i,1)
				If char = "{" Then Exit ; ende des befehls
				comm$ = comm$ + char
			Wend 
			While i < Len(txt$)
				i = i + 1
				char$ = Mid(txt$,i,1)
				If char = "}" Then Exit ; ende des parameters
				para = para + char
			Wend 
			Select comm
			Case "progr" ; progressbar
				subsur = CreateSurface(mesh, f\brush2)
				vert[0] = AddVertex(subsur, x#,y+f\height/2.0,0) ; ol
				vert[1] = AddVertex(subsur, x#,y+f\height/4.0,0) ; ul
				vert[2] = AddVertex(subsur, x#+Float(f\width)*(Float(para)/20.0),y+f\height/2.0,0) ; or
				vert[3] = AddVertex(subsur, x#+Float(f\width)*(Float(para)/20.0),y+f\height/4.0,0) ; ur
				AddTriangle(subsur,vert[2],vert[1],vert[0])
				AddTriangle(subsur,vert[1],vert[2],vert[3])
				x = x + f\width * 4.7
			End Select
		Default
			ascii = ascii-33
			If ascii >= 0 And ascii < 94 Then 
				If ascii < 47 Then u# = ascii * txt_width * 2 +.001 Else u# = (ascii-47) * txt_width * 2 +.001
				
				vert[0] = AddVertex(sur, x#,y+f\height,0,				u#,0+(ascii>46)*.5) ; ol
				vert[1] = AddVertex(sur, x#,y,0,						u#,.5+(ascii>46)*.5) ; ul
				vert[2] = AddVertex(sur, x#+f\width*2,y+f\height,0,	u#+txt_width*2-.002,0+(ascii>46)*.5) ; or
				vert[3] = AddVertex(sur, x#+f\width*2,y,0,			u#+txt_width*2-.002,.5+(ascii>46)*.5) ; ur
				
				 x# = x# + f\widths[ascii]/7
				AddTriangle(sur,vert[2],vert[1],vert[0])
				AddTriangle(sur,vert[1],vert[2],vert[3])
			EndIf
		End Select
	Next
	
	NameEntity mesh,txt$
	
	Return mesh
End Function

Function Txt_UpdateText(mesh, txt$, font, par=0, align=0, maxwidth=10000)
	If mesh = 0
		Return Txt_Text(txt$, font, par, align, maxwidth)
	ElseIf Not EntityName(mesh) = txt$
		FreeEntity mesh
		Return Txt_Text(txt$, font, par, align, maxwidth)
	Else
		Return mesh
	End If
End Function

Function Txt_TextToMesh(txt$, font, mesh, align=0, maxwidth=10000)
	For f.txt_font = Each txt_font
		If f\tex = font Then Exit
	Next
	
	txt$ = Replace(txt$,"ä","ae")
	txt$ = Replace(txt$,"ß","ss")
	txt$ = Replace(txt$,"ö","oe")
	txt$ = Replace(txt$,"ü","ue")
	
	sur = GetSurface(mesh,1)
	ClearSurface sur
	
	Local vert[3]
	
	x# = Len(txt) * f\width * align
	
	For i = 1 To Len(txt$)
		Ascii = Asc(Mid(txt$,i,1))
		
		Select ascii
		Case 9
			i = i + 1
			Ascii = Asc(Mid(txt$,i,1))-48
			x# = 22*ascii
		Case 13
			x# = 0
			y# = y-f\height
		Case 32
			vert[0] = AddVertex(sur, x#,y+f\height,0,			0,0) ; ol
			vert[1] = AddVertex(sur, x#,y,0,					0,0) ; ul
			vert[2] = AddVertex(sur, x#+f\width,y+f\height,0,	0,0) ; or
			vert[3] = AddVertex(sur, x#+f\width,y,0,			0,0) ; ur
			
			x# = x# + f\width
			AddTriangle(sur,vert[2],vert[1],vert[0])
			AddTriangle(sur,vert[1],vert[2],vert[3])
			If x > maxwidth Then
				x# = 0
				y# = y-f\height
			EndIf
		Default
			ascii = ascii-33
			If ascii >= 0 And ascii < 94 Then 
				If ascii < 47 Then u# = ascii * txt_width * 2 +.001 Else u# = (ascii-47) * txt_width * 2 +.001
				
				vert[0] = AddVertex(sur, x#,y+f\height,0,				u#,0+(ascii>46)*.5) ; ol
				vert[1] = AddVertex(sur, x#,y,0,						u#,.5+(ascii>46)*.5) ; ul
				vert[2] = AddVertex(sur, x#+f\width*2,y+f\height,0,	u#+txt_width*2,0+(ascii>46)*.5) ; or
				vert[3] = AddVertex(sur, x#+f\width*2,y,0,			u#+txt_width*2,.5+(ascii>46)*.5) ; ur
				
				 x# = x# + f\widths[ascii]/7
				AddTriangle(sur,vert[2],vert[1],vert[0])
				AddTriangle(sur,vert[1],vert[2],vert[3])
			EndIf
		End Select
	Next
	
	EntityTexture mesh,f\tex
	EntityFX mesh,1+8+16
	NameEntity mesh,txt$
	
	Return mesh
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D