Global Event_ID

Type Event
	Field id
	Field name$, para$
	Field source
End Type


Function Event_NewEvent.Event(name$, para$, source)
	e.Event = New Event
	e\id	= Event_ID
	e\name	= name
	e\para	= para
	e\source= source
	
	Event_ID = Event_ID + 1
	
	Return e
End Function

Function Event_HandleEvents()
	For e.Event = Each Event
		For v.Prs_Void = Each Prs_Void
			If v\condition = e\name And v\active = 1
				If v\csource = "any"
					Prs_InterpretEvent(v,e)
				Else
					ttxt0$ = v\csource
					While Util_GetParas(ttxt0,",")
						If paras[0] = e\source Then Prs_InterpretEvent(v,e)
						ttxt0 = paras[1]
					Wend
					If ttxt0 = e\source Then Prs_InterpretEvent(v,e)
				EndIf
				If v\updaterate = 0 Then Exit
			EndIf
		Next
		
		Delete e
	Next
End Function