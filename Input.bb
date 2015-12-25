Global mx,my,mlh, mrh
Global mh
Global mxs, mys

Global gkey$

HidePointer
	

Global Select_byRect
Global byrectx, byrecty
Global byrectw, byrecth


Function Inp_Mouse()
	mx = MouseX()
	my = MouseY()
	mxs= MouseXSpeed()
	mys= MouseYSpeed()
	
	If mlh = 0 Then
		If MouseDown(1) Then mlh = 1
	ElseIf mlh = 1 Then
		If MouseDown(1) Then mlh = 2 Else mlh = 0
	ElseIf mlh = 2
		If MouseDown(1) = 0 Then mlh = 0
	EndIf

	If mrh = 0 Then
		If MouseDown(2) Then mrh = 1
	ElseIf mrh = 1 Then
		If MouseDown(2) Then mrh = 2 Else mrh = 0
	ElseIf mrh = 2
		If MouseDown(2) = 0 Then mrh = 0
	EndIf

	mh = mlh
	Select mlh
		Case 1
			byrectx = mx
			byrecty = my
			Select_byRect = 1
			byrectw = 5
			byrecth = 5
			
		Case 2
			byrectw = mx-byrectx
			byrecth = my-byrecty
			Select_byRect = 1
			
		Default
			If Select_byRect = 1 Then 
				Select_byRect = 2
			Else
				Select_byRect = 0
			EndIf
	End Select
	
	tkey = GetKey()
	If tkey<>0
		gkey$ = Chr$(tkey)
	Else
		gkey$ = ""
	EndIf
End Function

Function Inp_KeyHit(key)
	If key <= 255 Then
		Return KeyHit(key)
	ElseIf key = 256
		Return (mlh = 2) Or (mh = 1)
	ElseIf key = 257
		Return (mrh = 2) Or (mrh And menu_options <> 0)
	ElseIf key < 260
		Return MouseHit(key-255)
	ElseIf key = 261
		Return MouseZSpeed()>0
	ElseIf key = 262
		Return MouseZSpeed()<0
	ElseIf key < 270
		Return JoyHit(key-262)
	EndIf
End Function

Function Inp_KeyDown(key)
	If key <= 255 Then
		Return KeyDown(key)
	ElseIf key < 261
		Return MouseDown(key-255)
	ElseIf key = 261
		Return MouseZSpeed()>0
	ElseIf key = 262
		Return MouseZSpeed()<0
	ElseIf key < 270
		Return JoyDown(key-262)
	EndIf
End Function
;~IDEal Editor Parameters:
;~C#Blitz3D