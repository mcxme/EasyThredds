The service can be called to translate spatial queries into a variaty of protocols.
Currently, OPeNDAP, CdmRemmote and NCSS are supported.
A custom query format is used and translated by EasyThredds.
Therefore, the URL has to match the following pattern:
\<EasyThredds base URL\>/\<protocol name\>/translate/\<dataset\>?\<query\>

The protocol can be omitted to let EasyThredds pick the best protocol automatically.
Otherwise, it has to be one of [cdmremote, opendap, ncss].
The endpoint THREDDS catalogue has to be specified in the ressource file 'config.properties'.

The query can contain the following elements.
<table>
    <tr>
        <td>Name</td>
        <td>Explanation</td>
        <td>Type</td>
        <td>Condition</td>
        <td>Example</td>
    </tr>
    <tr>
        <td>vars</td>
        <td>The variables to be fetched</td>
        <td>list of textual variable names</td>
        <td>at least one variable is required</td>
        <td>vars=aclc</td>
    </tr>
    <tr>
        <td>lat</td>
        <td>The latitude subset range</td>
        <td>spatial range</td>
        <td>The range has to be within the boundaries 0.0 to 360.0</td>
        <td>lat=[12.4;1;17.8]</td>
    </tr>
    <tr>
        <td>lon</td>
        <td>The longitude subset range</td>
        <td>spatial range</td>
        <td>The range has to be within the boundaries -90.0 to 90.0</td>
        <td>lat=[4.0;;18.9]</td>
    </tr>
    <tr>
        <td>lev</td>
        <td>The altitude subset range</td>
        <td>numeric range</td>
        <td>-</td>
        <td>lat=[0:1:5]</td>
    </tr>
    <tr>
        <td>time</td>
        <td>The time subset range</td>
        <td>time range</td>
        <td>The date time format is 'dd/MM/yyyy-HH:mm:ss'</td>
        <td>lat=[20/08/1992-20:00:00;1;22/02/1999-12:00:00]</td>
    </tr>
</table>

Each range has a start value, a stride and an end value.
If the stride is omitted the default stride of 1 is assumed.
The start value has to be smaller or equal to the end value.
