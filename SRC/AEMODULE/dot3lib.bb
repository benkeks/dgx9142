; Dot3Mapping Library
; by INpac


Type Dot3Light
	Field ent
	Field mul#
	Field typ
End Type

; Dot3-Ojects
Function Dot3_ParseDot3(Stream,o.obj)

	dot3scaleU#	= 1.0
	dot3scaleV#	= 1.0
	
	Repeat
		lin$ = Trim(ReadLine(stream))
		If Util_GetParas(lin$)
			Select Lower(Trim(paras[0]))
			Case "path"
				path$		= paras[1]
			Case "scaleu"
				dot3scaleu#	= paras[1]
			Case "scalev"
				dot3scalev#	= paras[1]

			End Select
		EndIf
	Until lin="}"

	
	If path$ <> "" Then Dot3_CreateDot3( o, path,dot3scaleu,dot3scalev )

End Function

Function Dot3_CreateDot3( o.obj, path$,scaleX#,scaleY# )

	o\dot3_tex	= LoadTexture( path )
	If o\dot3_tex = 0 Then Return False

	TextureBlend o\dot3_tex, 4
	ScaleTexture o\dot3_tex, scaleX#,scaleY#
	EntityTexture o\entity, o\dot3_tex,0,0
	EntityFX o\entity,1+2

End Function


; Lights und Obj-unabhängiges
Function Dot3_UpdateDot3( o.obj )

	If o\dot3_tex = 0 Or EntityClass(o\entity)<>"Mesh" Then Return False
	mesh = o\entity

	EP=GetParent(mesh)
	If EP	EntityParent mesh,0,1

	n_surf = CountSurfaces(mesh)
	For s = 1 To n_surf
		surf = GetSurface(mesh,s)
		n_vert = CountVertices(surf)-1
		For v = 0 To n_vert
			red2# = 0
			grn2# = 0
			blu2# = 0
			For d3l.Dot3Light = Each Dot3Light
				lx#=EntityX(d3l\ent,True)
				ly#=EntityY(d3l\ent,True)
				lz#=EntityZ(d3l\ent,True)
	
				If d3l\typ = 1 ; Directional light
					TFormVector 0,0,1,d3l\ent,0
					nx# = TFormedX()
					ny# = TFormedY()
					nz# = TFormedZ()
					TFormNormal VertexNX(surf,v),VertexNY(surf,v),VertexNZ(surf,v),mesh,0
					red# = TFormedX()
					grn# = TFormedY()
					blu# = TFormedZ()
					
					
				ElseIf d3l\typ = 2 ; Point light		
					; Vertex Normal in World coordinates
					TFormNormal VertexNX(surf,v),VertexNY(surf,v),VertexNZ(surf,v),mesh,0
					Vnx# = TFormedX()
					Vny# = TFormedY()
					Vnz# = TFormedZ()
					
					; Vertex > Light Vector in World coordinates
					TFormPoint VertexX(surf,v),VertexY(surf,v),VertexZ(surf,v),mesh,0
					Lvx# = lx - TFormedX()
					Lvy# = ly - TFormedY()
					Lvz# = lz - TFormedZ()

					; Normalize Vertex > Light Vector
					d# = Sqr(Lvx*Lvx + Lvy*Lvy + Lvz*Lvz)
					Lvx	=	Lvx / d
					Lvy = Lvy / d
					Lvz = Lvz / d
				End If

				; Theta Angle between Vertex Normal & Vertex>Light Normal
				dot# = (Lvx*Vnx + Lvy*Vny + Lvz*Vnz)

				; Clamp Colors to 0
				If dot<0.0 Then dot# = 0
				
				; If the Mesh had a Parent, Convert Light Vector into that Parents Local coordinates
				; unsure if this'll work with multiple hierarchy
				If EP
					TFormNormal Lvx,Lvy,Lvz,0,EP
					Lvx# = TFormedX()
					Lvy# = TFormedY()
					Lvz# = TFormedZ()
				End If
				
				red# = ( (1.0+( Lvx * dot)) * 127) * d3l\mul
				grn# = ( (1.0+( Lvy * dot)) * 127) * d3l\mul
				blu# = ( (1.0+( -Lvz * dot)) * 127) * d3l\mul
				
				red2# = red2+red
				grn2# = grn2+grn
				blu2# = blu2+blu
			Next
			VertexColor surf,v,red2,grn2,blu2

			;DebugLog "licht r"+red2+" g "+grn2+" b "+blu2

		Next
	Next
	If EP EntityParent mesh,EP,1
End Function

Function Dot3_RegLight( light,typ )

	d3l.Dot3Light = New Dot3Light
	
	d3l\ent = light

	d3l\typ = typ
	d3l\mul = 1

	Return d3l\ent
End Function

Function Dot3_CreateLight(typ=1,parent=0,mul#=1.0,real=True)
	
	d3l.Dot3Light = New Dot3Light
	
	If real
		d3l\ent = CreateLight(typ,parent)
		LightRange d3l\ent,50
	Else
		d3l\ent = CreatePivot(parent)
	End If

	d3l\typ = typ
	d3l\mul = mul
	
	Return d3l\ent

End Function

Function Dot3_LightRange(ent,range#)
	For d3l.dot3light = Each dot3light
		If d3l\ent = ent
			If Lower$(EntityClass(d3l\ent))="light"
				LightRange d3l\ent,range
				Return
			End If
		End If
	Next
	
End Function

Function Dot3_LightIntensity(ent,intens#)
	For d3l.dot3light = Each dot3light
		If d3l\ent = ent
			d3l\mul = intens
			If Lower$(EntityClass(d3l\ent))="light"
				LightColor d3l\ent,d3l\mul*255.0,d3l\mul*255.0,d3l\mul*255.0
			End If			
			Return
		End If
	Next
End Function