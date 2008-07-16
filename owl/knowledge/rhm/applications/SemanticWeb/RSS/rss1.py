# KEHOME/knowledge/application/SemanticWeb/RSS/rss1.py
# Dec/22/2002

# Mark Pilgrim, "What is RSS?", December 18, 2002.
# http://www.xml.com/pub/a/2002/12/18/dive-into-xml.html

from xml.dom import minidom
import urllib

DEFAULT_NAMESPACES = \
  (None, # RSS 0.91, 0.92, 0.93, 0.94, 2.0
  'http://purl.org/rss/1.0/', # RSS 1.0
  'http://my.netscape.com/rdf/simple/0.9/' # RSS 0.90
  )
DUBLIN_CORE = ('http://purl.org/dc/elements/1.1/',)

def load(rssURL):
  return minidom.parse(urllib.urlopen(rssURL))

def getElementsByTagName(node, tagName, possibleNamespaces=DEFAULT_NAMESPACES):
  for namespace in possibleNamespaces:
    children = node.getElementsByTagNameNS(namespace, tagName)
    if len(children): return children
  return []

def first(node, tagName, possibleNamespaces=DEFAULT_NAMESPACES):
  children = getElementsByTagName(node, tagName, possibleNamespaces)
  return len(children) and children[0] or None

def textOf(node):
  return node and "".join([child.data for child in node.childNodes]) or ""

if __name__ == '__main__':
  import sys
  rssDocument = load(sys.argv[1])
  for item in getElementsByTagName(rssDocument, 'item'):
    print 'title:', textOf(first(item, 'title'))
    print 'link:', textOf(first(item, 'link'))
    print 'description:', textOf(first(item, 'description'))
    print 'date:', textOf(first(item, 'date', DUBLIN_CORE))
    print 'author:', textOf(first(item, 'creator', DUBLIN_CORE))
    print

