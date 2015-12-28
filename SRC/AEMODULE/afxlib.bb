Function FX_LoadAFX(path$,parent=0)
	Util_CheckFile(path$)
	stream = ReadFile(path)
	Return FX_ParseAFX(stream,parent)
End Function


Function FX_ParseAFX(stream,parent=0)
	Repeat
		lin$ = Lower(Trim(ReadLine(stream)))
		Select lin
		Case "emitter{"
			;mesh = FX_ParseEmitter(stream,parent)
		Case "sprite{"
			mesh = FX_ParseSprite(stream,parent)
		Case "light{"
			mesh = FX_ParseLight(stream,parent)
		Case "explo{"
			mesh = FX_ParseExplo(stream,parent)
		Case "sfx{"
			mesh = Aud_ParseSfx(stream,parent)
		Case "}"
			Exit
		Default
			If Util_GetParas(lin)
				Select paras[0]
				Case "fromfile"
					stream2 = ReadFile(paras[1])
					mesh = FX_ParseAFX(stream2,parent) 
				End Select
			EndIf
			If Left(Util_Trim2(Lower(lin)),1) = "<"
				Main_ParseDetail(stream, lin)
			EndIf
		End Select
	Until Eof(stream)
	Return mesh
End Function

Function FX_FreeAFX(entity)
	FreeEntity entity
End Function

Function FX_ParseExplo(stream,par)
	mesh = CreatePivot(par)
	Repeat
		lin$ = Trim(ReadLine(stream))
		Select lin$
		Case "aem{"
			AEM_Parse(stream,mesh)
		Case "child{"
			FX_ParseAFX(stream,mesh)
		Default
			If Util_GetParas(lin$)
				Select paras[0]
				Case "x"
					x#	= paras[1]
				Case "y"
					y#	= paras[1]
				Case "z"
					z#	= paras[1]
				Case "endtime"
					et 	= paras[1]
				
				Case "sx"
					sx#	= paras[1]
				Case "sy"
					sy#	= paras[1]
				
				Case "r"
					r	= paras[1]
				Case "g"
					g	= paras[1]
				Case "b"
					b		= paras[1]
				End Select
			EndIf
		End Select
	Until lin="}"
	
	mesh2 = FX_CreateExplosion(EntityX(mesh)+x#,EntityY(mesh)+y#,EntityZ(mesh)+z#,sx#,sy#,r,g,b,et)
	FreeEntity mesh
	mesh = mesh2
	EntityParent mesh,par
	
	Return mesh
End Function


Function FX_ParseSprite(stream,par)
	mesh = CreateSprite(par)
	Local sx = 1, sy = 1
	Repeat
		lin$ = Trim(ReadLine(stream))
		Select lin$
		Case "aem{"
			AEM_Parse(stream,mesh)
		Case "child{"
			FX_ParseAFX(stream,mesh)
		Default
			If Util_GetParas(lin$)
				Select paras[0]
				Case "rotate"
					RotateSprite mesh,paras[1]
				Case "viewmode"
					SpriteViewMode mesh,paras[1]
				Case "mode"
					mode	= paras[1]
				Case "path"
					FreeEntity mesh
					DebugLog AvailVidMem()
					mesh = LoadSprite(paras[1],mode,par)
				Case "handlex"
					handlex = paras[1]
				Case "handley"
					handley = paras[1]
				Case "scalex"
					sx		= paras[1]
				Case "scaley"
					sy		= paras[1]
				End Select
			EndIf
		End Select
	Until lin="}"
	HandleSprite mesh,handlex,handley
	ScaleSprite mesh,sx,sy
	Return mesh
End Function

Function FX_ParseLight(stream,par)
	Repeat
		lin$ = Trim(ReadLine(stream))
		Select lin$
		Case "aem{"
			AEM_Parse(stream,mesh)
		Case "child{"
			FX_ParseAFX(stream,mesh)
		Default
			If Util_GetParas(lin$)
				Select paras[0]
				Case "type"
					mesh = CreateLight(paras[1],par)
					typ = paras[1]
				Case "red"
					r = paras[1]
				Case "green"
					g = paras[1]
				Case "blue"
					b = paras[1]
				Case "range"
					range = paras[1]
				End Select
			EndIf
		End Select
	Until lin="}"
	LightColor mesh,r,g,b
	LightRange mesh,range
	
	Dot3_RegLight( mesh,2 )
	;If main_shadows Then SetShadowLight(mesh)
	
	Return mesh
End Function