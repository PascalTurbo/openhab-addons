# OchsnerWeb2Com Binding

Bridge: Communication connection to talk with the heatpump
Thing: The heatpump itself (OID 1/2), also all single components of the Heatpump ( Service Report, Waermepumpe, Zusatzheizung, Warmwasserkreis, ...)
Channel: All attributes of the single components like Energy Consumption, Temperatures, ...


Aufgaben:

- Add thing property "name" (Already set in thing-types.xml) and set them while initializing the binding
- Einen numerischen Channel definieren und basierend auf den ermittelten Werten mit UOM anlegen und mit einem Item verknüpfen
- Herausfinden, was es mit "config-description-ref" auf sich hat und wo es nützlich sein kann
- Wenn das funktioniert, alle supporteten Item Types aufnehmen

## Namen der einzelnen Datenfelder

Request gegen http://192.168.2.239/res/xml/VarIdentTexte_de.xml liefert ein XML mit allen Namen.
Bspw. ist 00:08 == IST Temp.TWR
          02:53 == Status Wärmeerzeuger
Dies leitet sich aus der XML-Struktur ab - sollte recht einfach zu parsen sein.
Der Wert steckt im Feld dpCfg - name

## Name der Ebenen (Unter den Root-Gruppen wie bspw. WAERMEPUMPE)

Request gegen http://192.168.2.239/res/xml/EbenenTexte_de.xml liefert ein XML mit den Ebenennamen
Die Werte finden sich, indem man DataPointConfigurations vom value "composite" findet, der Typ entspricht dann dem fcttyp, der index der ebene id

## Enum Felder

Die Werte kommen aus http://192.168.2.239/res/xml/AufzaehlTexte_de.xml
Das Mapping ergibt sich auch hier aus dem Namen.
Für die Anzeige reicht das einfache Mapping. Soll geschrieben werden, dann muss aus dpCfg.prop (<prop>-r-- enum = 0,5,6</prop>) die Liste der erlaubten Werte ausgelesen werden

## Error Teste

Die Werte kommen aus http://192.168.2.239/res/xml/ErrorTexte_de.xml
Unklar bisher, wo diese genutzt werden


Zukunft:

- Write support implementieren (thing-types.xml read only beachten)


Supported Item Types:


It must be one of the following:
 - Call
 - Color
 - Contact
 - DateTime
 - Dimmer
 - Group
 - Image
 - Location
 - Number
 - Number:Acceleration
 - Number:AmountOfSubstance
 - Number:Angle
 - Number:Area
 - Number:ArealDensity
 - Number:CatalyticActivity
 - Number:Currency
 - Number:DataAmount
 - Number:DataTransferRate
 - Number:Density
 - Number:Dimensionless
 - Number:ElectricCapacitance
 - Number:ElectricCharge
 - Number:ElectricConductance
 - Number:ElectricConductivity
 - Number:ElectricCurrent
 - Number:ElectricInductance
 - Number:ElectricPotential
 - Number:ElectricResistance
 - Number:EmissionIntensity
 - Number:Energy
 - Number:EnergyPrice
 - Number:Force
 - Number:Frequency
 - Number:Illuminance
 - Number:Intensity
 - Number:Length
 - Number:LuminousFlux
 - Number:LuminousIntensity
 - Number:MagneticFlux
 - Number:MagneticFluxDensity
 - Number:Mass
 - Number:Power
 - Number:Pressure
 - Number:RadiationDoseAbsorbed
 - Number:RadiationDoseEffective
 - Number:RadiationSpecificActivity
 - Number:RadioactiveActivity
 - Number:SolidAngle
 - Number:Speed
 - Number:Temperature
 - Number:Time
 - Number:Volume
 - Number:VolumetricFlowRate
 - Player
 - Rollershutter
 - String
 - Switch







_Give some details about what this binding is meant for - a protocol, system, specific device._

_If possible, provide some resources like pictures (only PNG is supported currently), a video, etc. to give an impression of what can be done with this binding._
_You can place such resources into a `doc` folder next to this README.md._

_Put each sentence in a separate line to improve readability of diffs._

## Supported Things

_Please describe the different supported things / devices including their ThingTypeUID within this section._
_Which different types are supported, which models were tested etc.?_
_Note that it is planned to generate some part of this based on the XML files within ```src/main/resources/OH-INF/thing``` of your binding._

- `bridge`: Short description of the Bridge, if any
- `sample`: Short description of the Thing with the ThingTypeUID `sample`

## Discovery

_Describe the available auto-discovery features here._
_Mention for what it works and what needs to be kept in mind when using it._

## Binding Configuration

_If your binding requires or supports general configuration settings, please create a folder ```cfg``` and place the configuration file ```<bindingId>.cfg``` inside it._
_In this section, you should link to this file and provide some information about the options._
_The file could e.g. look like:_

```
# Configuration for the OchsnerWeb2Com Binding
#
# Default secret key for the pairing of the OchsnerWeb2Com Thing.
# It has to be between 10-40 (alphanumeric) characters.
# This may be changed by the user for security reasons.
secret=openHABSecret
```

_Note that it is planned to generate some part of this based on the information that is available within ```src/main/resources/OH-INF/binding``` of your binding._

_If your binding does not offer any generic configurations, you can remove this section completely._

## Thing Configuration

_Describe what is needed to manually configure a thing, either through the UI or via a thing-file._
_This should be mainly about its mandatory and optional configuration parameters._

_Note that it is planned to generate some part of this based on the XML files within ```src/main/resources/OH-INF/thing``` of your binding._

### `sample` Thing Configuration

| Name            | Type    | Description                           | Default | Required | Advanced |
|-----------------|---------|---------------------------------------|---------|----------|----------|
| hostname        | text    | Hostname or IP address of the device  | N/A     | yes      | no       |
| password        | text    | Password to access the device         | N/A     | yes      | no       |
| refreshInterval | integer | Interval the device is polled in sec. | 600     | no       | yes      |

## Channels

_Here you should provide information about available channel types, what their meaning is and how they can be used._

_Note that it is planned to generate some part of this based on the XML files within ```src/main/resources/OH-INF/thing``` of your binding._

| Channel | Type   | Read/Write | Description                 |
|---------|--------|------------|-----------------------------|
| control | Switch | RW         | This is the control channel |

## Full Example

_Provide a full usage example based on textual configuration files._
_*.things, *.items examples are mandatory as textual configuration is well used by many users._
_*.sitemap examples are optional._

### Thing Configuration

```java
Example thing configuration goes here.
```
### Item Configuration

```java
Example item configuration goes here.
```

### Sitemap Configuration

```perl
Optional Sitemap configuration goes here.
Remove this section, if not needed.
```

## Any custom content here!

_Feel free to add additional sections for whatever you think should also be mentioned about your binding!_
