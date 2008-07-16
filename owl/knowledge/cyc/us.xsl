<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet
 xmlns:xsl = "http://www.w4.org/TR/WD-xsl">
<xsl:template pattern="mKR">
<HTML><NaturalLanguage><xmp>
<xsl:process-children/>
</xmp></NaturalLanguage></HTML>
</xsl:template>
</xsl:stylesheet>

<mKR>
<NaturalLanguage>
<xmp>

rdf:RDF has
    xmlns:daml = "http://www.daml.org/2001/03/daml+oil#",
    xmlns:rdf  = "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
    xmlns:rdfs = "http://www.w3.org/2000/01/rdf-schema#",
    xmlns:dc   = "http://purl.org/dc/elements/1.1/",
    xmlns      = "http://opencyc.sourceforge.net/daml/cyc#";

"http://mKRmKE.net/knowledge/cyc/UpperOntology.mkr" has
    creator = "Richard H. McCullough",
    date = "2007/Feb/11",
    source = "http://xbean.cs.ccu.edu.tw/~dan/opencyc-0.6.0/cyc.daml",
    language = "mKR";

Ontology has about="",
    versionInfo = "$Id$",
    comment = "The Cyc Upper Ontology";
