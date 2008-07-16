use SOAP::Lite;
my $key='000000000000000000000000';
my $query="foo";
my $googleSearch = SOAP::Lite -> service("file:GoogleSearch.wsdl");
my $result = $googleSearch -> doGoogleSearch($key, $query, 0, 10, "false", "", "false", "", "latin1", "latin1");
print "About $result->{'estimatedTotalResultsCount'} results.\n";
