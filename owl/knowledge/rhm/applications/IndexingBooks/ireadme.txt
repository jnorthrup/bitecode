# 11:00 am 1998/1/9

#==========#
# indexing #
#==========#

# The files in the KEHOME/indexing directory pertain to
# both a back-of-the-book index,
# and a library-book index.

# back-of-the-book index
#----------------------#
# The files in the KEHOME/indexing directory
# support all the conceptual functions required to
# create a back-of-the-book index.
# Some details, such as requirements for final
# ordering and formatting, are not explicitly
# considered.

# The steps in creating an index are

# 1. record the concepts to be indexed in xtopic.rel
#	topic			short English phrase
#	reference-locator list	chapter/section/page
# NOTE: Current KE implementation requires quotes
# enclosing phrases containing "and","&","or","|".

# 2. integrate the concepts into entry/subentry groups
#    and record in xentry.rel  (This step may be done
#    before or concurrently with step 1.)
#	topic
#	entry		first level index concept
#	subentry	second/third/... level index concept

# 3. record cross-references in xsee.rel
#	entry
#	xtype		see or see also
#	xref		cross-reference list

# 4. transform entry/subentry into heading/subheading format
#    required in index, and record in xheading.rel
#	entry
#	heading


# library-book index
#------------------#
# The files in the KEHOME/indexing directory provide access to
# the "Find-It-Faster" index developed by the Bensenville, IL
# Public Library.  The purpose of this index is to help the user
# locate a book on a particular subject.  By mapping this index
# to the Dewey Decimal hierarchy, you can easily find books
# on related subjects.

# The steps in creating and using this index are

# 1. record the subject/ddcn relation in dsubject.rel
#	ddcn		Dewey Decimal call numbers
#	subject		subject heading
# load the lattice using dlat.ku

# 2. measure additional attriubutes using dmeasure.ku

# 3. classify the subjects into a subject/ddcn lattice
#    by differentiating as in dclass.ku

# 4. explore the subject/ddcn lattice as in dexlat.ku


# The same techniques can be applied directly to the
# Dewey Decimal Classification book/ddcn hierarchy.

# 5. record the book/ddcn hierarchy in d100.ho,d010.ho,d001.ho 
# load the hierarchy using dhier.ku

# 6. explore the book/ddcn hierarchy as in dexhier.ku
