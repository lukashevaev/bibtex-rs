<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
				xmlns="http://datacite.org/schema/kernel-4" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
				xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:exsl="http://exslt.org/common"
				extension-element-prefixes="exsl">
	<xsl:output method="xml" indent="no" standalone="no"
				encoding="utf-8" omit-xml-declaration="yes" />

	<xsl:param name="default.publisher"
			   select="'Санкт-Петербургский политехнический университет Петра Великого'" />

	<xsl:template match='record'>
		<xsl:variable name="lang">
			<xsl:call-template name="getLanguage" />
		</xsl:variable>
		<resource
				xsi:schemaLocation="http://datacite.org/schema/kernel-4 http://schema.datacite.org/meta/kernel-4/metadata.xsd">
			<xsl:if
					test="field[@id='856']/subfield[@id='u' and (starts-with(., 'doi:') or starts-with(., 'http://doi.org/') or starts-with(., 'https://doi.org/'))] or field[@id='017' and subfield[@id='2']='doi']/subfield[@id='a']">
				<identifier identifierType="DOI">
					<xsl:choose>
						<xsl:when
								test="field[@id='856']/subfield[@id='u' and starts-with(., 'doi:')]">
							<xsl:value-of
									select="substring-after(field[@id='856']/subfield[@id='u' and starts-with(., 'doi:')][1], 'doi:')" />
						</xsl:when>
						<xsl:when test="field[@id='856']/subfield[@id='u' and starts-with(., 'http://doi.org/')]">
							<xsl:value-of
									select="substring-after(field[@id='856']/subfield[@id='u' and starts-with(., 'http://doi.org/')][1], 'http://doi.org/')" />
						</xsl:when>
						<xsl:when test="field[@id='856']/subfield[@id='u' and starts-with(., 'https://doi.org/')]">
							<xsl:value-of
									select="substring-after(field[@id='856']/subfield[@id='u' and starts-with(., 'https://doi.org/')][1], 'https://doi.org/')" />
						</xsl:when>
						<xsl:otherwise>
							<xsl:value-of
									select="field[@id='017' and subfield[@id='2']='doi']/subfield[@id='a']" />
						</xsl:otherwise>
					</xsl:choose>
				</identifier>
			</xsl:if>

			<creators>
				<xsl:choose>
					<xsl:when
							test="field[@id='700' or @id='701'] or field[@id='461' or @id='463']/subfield[@id='1']/field[@id='700' or @id='701'] or field[@id='710' or @id='711'] or field[@id='461' or @id='463']/subfield[@id='1']/field[@id='710' or @id='711']">
						<xsl:for-each
								select="field[@id='700' or @id='701'] | field[@id='461' or @id='463']/subfield[@id='1']/field[@id='700' or @id='701']">
							<xsl:call-template name="personalCreator" />
						</xsl:for-each>
						<xsl:for-each
								select="field[@id='710' or @id='711'] | field[@id='461' or @id='463']/subfield[@id='1']/field[@id='710' or @id='711']">
							<xsl:call-template name="corporateCreator" />
						</xsl:for-each>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when
									test="field[@id='702'] or field[@id='461' or @id='463']/subfield[@id='1']/field[@id='702']">
								<xsl:for-each
										select="(field[@id='702'] | field[@id='461' or @id='463']/subfield[@id='1']/field[@id='702'])[1]">
									<xsl:call-template name="personalCreator" />
								</xsl:for-each>
							</xsl:when>
							<xsl:when
									test="field[@id='712'] or field[@id='461' or @id='463']/subfield[@id='1']/field[@id='712']">
								<xsl:for-each
										select="(field[@id='712'] | field[@id='461' or @id='463']/subfield[@id='1']/field[@id='712'])[1]">
									<xsl:call-template name="corporateCreator" />
								</xsl:for-each>
							</xsl:when>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</creators>

			<titles>
				<title xml:lang="{$lang}">
					<xsl:value-of select="field[@id='200']/subfield[@id='a']" />
					<xsl:for-each select="field[@id='200']/subfield[@id='h' or @id='i']">
						<xsl:choose>
							<xsl:when test="@id='h'">
								<xsl:text>. </xsl:text>
								<xsl:value-of select="." />
							</xsl:when>
							<xsl:otherwise>
								<xsl:choose>
									<xsl:when test="preceding-sibling::subfield[1]/@id='h'">
										<xsl:text>, </xsl:text>
									</xsl:when>
									<xsl:otherwise>
										<xsl:text>. </xsl:text>
									</xsl:otherwise>
								</xsl:choose>
								<xsl:value-of select="." />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:for-each>
				</title>
				<xsl:if test="field[@id='200']/subfield[@id='e']">
					<title xml:lang="{$lang}" titleType="Subtitle">
						<xsl:value-of select="field[@id='200']/subfield[@id='e']" />
					</title>
				</xsl:if>
				<xsl:if test="field[@id='200']/subfield[@id='d']">
					<xsl:variable name="altLang">
						<xsl:call-template name="getLanguage">
							<xsl:with-param name="src"
											select="field[@id='200']/subfield[@id='z']" />
						</xsl:call-template>
					</xsl:variable>
					<title xml:lang="{$altLang}">
						<xsl:value-of select="field[@id='200']/subfield[@id='d']" />
					</title>
				</xsl:if>
			</titles>

			<publisher>
				<xsl:choose>
					<xsl:when test="field[@id='210']/subfield[@id='c' or @id='g']">
						<xsl:value-of select="field[@id='210']/subfield[@id='c' or @id='g'][1]" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when
									test="field[@id='463']/subfield[@id='1']/field[@id='210']/subfield[@id='c' or @id='g']">
								<xsl:value-of
										select="field[@id='463']/subfield[@id='1']/field[@id='210']/subfield[@id='c' or @id='g'][1]" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="$default.publisher" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</publisher>

			<publicationYear>
				<xsl:choose>
					<xsl:when test="field[@id='210']/subfield[@id='d']">
						<xsl:value-of select="field[@id='210']/subfield[@id='d'][1]" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when
									test="field[@id='463']/subfield[@id='1']/field[@id='210']/subfield[@id='d']">
								<xsl:value-of
										select="field[@id='463']/subfield[@id='1']/field[@id='210']/subfield[@id='d'][1]" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>Unknown</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</publicationYear>

			<xsl:if
					test="field[@id='600' or @id='601' or @id='602' or @id='605' or @id='606' or @id='607' or @id='610']">
				<subjects>
					<xsl:for-each select="field[@id='600']">
						<subject>
							<xsl:value-of select="subfield[@id='a']" />
							<xsl:choose>
								<xsl:when test="subfield[@id='g']">
									<xsl:text>, </xsl:text>
									<xsl:value-of select="subfield[@id='g']" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:if test="subfield[@id='b']">
										<xsl:text>, </xsl:text>
										<xsl:value-of select="subfield[@id='b']" />
									</xsl:if>
								</xsl:otherwise>
							</xsl:choose>
							<xsl:if test="subfield[@id='d']">
								<xsl:text> </xsl:text>
								<xsl:value-of select="subfield[@id='d']" />
							</xsl:if>
							<xsl:if test="subfield[@id='c']">
								<xsl:text> (</xsl:text>
								<xsl:for-each select="subfield[@id='c']">
									<xsl:if test="position() != 1">
										<xsl:text>, </xsl:text>
									</xsl:if>
									<xsl:value-of select="." />
								</xsl:for-each>
								<xsl:text>) </xsl:text>
							</xsl:if>
							<xsl:if test="subfield[@id='f']">
								<xsl:text>, </xsl:text>
								<xsl:value-of select="subfield[@id='f']" />
							</xsl:if>
						</subject>
					</xsl:for-each>
					<xsl:for-each select="field[@id='601']">
						<subject>
							<xsl:value-of select="subfield[@id='a']" />
							<xsl:for-each select="subfield[@id='b']">
								<xsl:text> &#x2014; </xsl:text>
								<xsl:value-of select="." />
							</xsl:for-each>
							<xsl:if test="subfield[@id='e'] or subfield[@id='f']">
								<xsl:text> (</xsl:text>
								<xsl:value-of select="subfield[@id='e']" />
								<xsl:if test="subfield[@id='f']">
									<xsl:text> ; </xsl:text>
									<xsl:value-of select="subfield[@id='f']" />
								</xsl:if>
								<xsl:text>)</xsl:text>
							</xsl:if>
						</subject>
					</xsl:for-each>
					<xsl:for-each select="field[@id='602']">
						<subject>
							<xsl:value-of select="subfield[@id='a']" />
							<xsl:if test="subfield[@id='f']">
								<xsl:text>, </xsl:text>
								<xsl:value-of select="subfield[@id='f']" />
							</xsl:if>
						</subject>
					</xsl:for-each>
					<xsl:for-each
							select="field[@id='605' or @id='606' or @id='607' or @id='610']/subfield[@id='a']">
						<subject>
							<xsl:value-of select="." />
						</subject>
					</xsl:for-each>
				</subjects>
			</xsl:if>

			<xsl:if
					test="field[@id='702' or @id='712'] or field[@id='461' or @id='463']/subfield[@id='1']/field[@id='702' or @id='712']">
				<contributors>
					<xsl:for-each
							select="field[@id='702'] | field[@id='461' or @id='463']/subfield[@id='1']/field[@id='702']">
						<xsl:variable name="contributorType">
							<xsl:call-template name="getContributorType" />
						</xsl:variable>
						<contributor contributorType="{$contributorType}">
							<contributorName>
								<xsl:value-of select="subfield[@id='a']" />
								<xsl:if test="subfield[@id='d']">
									<xsl:text> </xsl:text>
									<xsl:value-of select="subfield[@id='d']" />
								</xsl:if>
								<xsl:choose>
									<xsl:when test="subfield[@id='g']">
										<xsl:text>, </xsl:text>
										<xsl:value-of select="subfield[@id='g']" />
									</xsl:when>
									<xsl:otherwise>
										<xsl:if test="subfield[@id='b']">
											<xsl:text>, </xsl:text>
											<xsl:value-of select="subfield[@id='b']" />
										</xsl:if>
									</xsl:otherwise>
								</xsl:choose>
								<xsl:if test="subfield[@id='c']">
									<xsl:text> (</xsl:text>
									<xsl:for-each select="subfield[@id='c']">
										<xsl:value-of select="." />
										<xsl:if test="position() != last()">
											<xsl:text>;</xsl:text>
										</xsl:if>
									</xsl:for-each>
									<xsl:text>) </xsl:text>
								</xsl:if>
							</contributorName>
							<xsl:call-template name="givenName" />
							<xsl:call-template name="familyName" />
							<xsl:call-template name="nameId" />
							<xsl:for-each select="subfield[@id='p']">
								<affiliation>
									<xsl:value-of select="." />
								</affiliation>
							</xsl:for-each>
						</contributor>
					</xsl:for-each>

					<xsl:for-each
							select="field[@id='712'] | field[@id='461' or @id='463']/subfield[@id='1']/field[@id='712']">
						<xsl:variable name="contributorType">
							<xsl:call-template name="getContributorType" />
						</xsl:variable>
						<contributor contributorType="{$contributorType}">
							<contributorName>
								<xsl:value-of select="subfield[@id='a']" />
								<xsl:for-each select="subfield[@id='b']">
									<xsl:text>. </xsl:text>
									<xsl:value-of select="." />
								</xsl:for-each>
							</contributorName>
						</contributor>
					</xsl:for-each>
				</contributors>
			</xsl:if>

			<language>
				<xsl:value-of select="$lang" />
			</language>

			<xsl:variable name="resourceTypeGeneral">
				<xsl:call-template name="getResourceTypeGeneral" />
			</xsl:variable>
			<resourceType resourceTypeGeneral="{$resourceTypeGeneral}">
				<xsl:choose>
					<xsl:when
							test="substring(field[@id='105']/subfield[@id='a'], 5, 1) = '7'">
						<xsl:text>Academic thesis</xsl:text>
					</xsl:when>
					<xsl:when
							test="substring(field[@id='105']/subfield[@id='a'], 5, 1) = 'a'">
						<xsl:text>Bibliography</xsl:text>
					</xsl:when>
					<xsl:when
							test="substring(field[@id='105']/subfield[@id='a'], 5, 1) = 'b'">
						<xsl:text>Catalogue</xsl:text>
					</xsl:when>
					<xsl:when
							test="substring(field[@id='105']/subfield[@id='a'], 5, 1) = 'c'">
						<xsl:text>Index</xsl:text>
					</xsl:when>
					<xsl:when
							test="substring(field[@id='105']/subfield[@id='a'], 5, 1) = 'd'">
						<xsl:text>Abstract</xsl:text>
					</xsl:when>
					<xsl:when
							test="substring(field[@id='105']/subfield[@id='a'], 5, 1) = 'e'">
						<xsl:text>Dictionary</xsl:text>
					</xsl:when>
					<xsl:when
							test="substring(field[@id='105']/subfield[@id='a'], 5, 1) = 'f'">
						<xsl:text>Encyclopaedia</xsl:text>
					</xsl:when>
					<xsl:when
							test="substring(field[@id='105']/subfield[@id='a'], 5, 1) = 'g'">
						<xsl:text>Directory</xsl:text>
					</xsl:when>
					<xsl:when
							test="substring(field[@id='105']/subfield[@id='a'], 5, 1) = 'h'">
						<xsl:text>Project description</xsl:text>
					</xsl:when>
					<xsl:when
							test="substring(field[@id='105']/subfield[@id='a'], 5, 1) = 'i'">
						<xsl:text>Statistics</xsl:text>
					</xsl:when>
					<xsl:when
							test="substring(field[@id='105']/subfield[@id='a'], 5, 1) = 'j'">
						<xsl:text>Programmed text book</xsl:text>
					</xsl:when>
					<xsl:when
							test="substring(field[@id='105']/subfield[@id='a'], 5, 1) = 'k'">
						<xsl:text>Patent</xsl:text>
					</xsl:when>
					<xsl:when
							test="substring(field[@id='105']/subfield[@id='a'], 5, 1) = 'l'">
						<xsl:text>Standard</xsl:text>
					</xsl:when>
					<xsl:when
							test="substring(field[@id='105']/subfield[@id='a'], 5, 1) = 'm'">
						<xsl:text>Dissertation</xsl:text>
					</xsl:when>
					<xsl:when
							test="substring(field[@id='105']/subfield[@id='a'], 5, 1) = 'n'">
						<xsl:text>Laws and Legislation</xsl:text>
					</xsl:when>
					<xsl:when
							test="substring(field[@id='105']/subfield[@id='a'], 5, 1) = 'o'">
						<xsl:text>Numeric table</xsl:text>
					</xsl:when>
					<xsl:when
							test="substring(field[@id='105']/subfield[@id='a'], 5, 1) = 'p'">
						<xsl:text>Technical report</xsl:text>
					</xsl:when>
					<xsl:when
							test="substring(field[@id='105']/subfield[@id='a'], 5, 1) = 'q'">
						<xsl:text>Examination paper</xsl:text>
					</xsl:when>
					<xsl:when
							test="substring(field[@id='105']/subfield[@id='a'], 5, 1) = 'r'">
						<xsl:text>Literature reviews</xsl:text>
					</xsl:when>
					<xsl:when
							test="substring(field[@id='105']/subfield[@id='a'], 5, 1) = 's'">
						<xsl:text>Treaties</xsl:text>
					</xsl:when>
					<xsl:when
							test="substring(field[@id='105']/subfield[@id='a'], 5, 1) = 't'">
						<xsl:text>Cartoons</xsl:text>
					</xsl:when>
					<xsl:when
							test="substring(field[@id='105']/subfield[@id='a'], 5, 1) = 'v'">
						<xsl:text>Dissertation revised</xsl:text>
					</xsl:when>
					<xsl:when
							test="substring(field[@id='105']/subfield[@id='a'], 5, 1) = 'w'">
						<xsl:text>Religious</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>Other</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</resourceType>

			<xsl:if test="field[@id='215']/subfield[@id='a' or @id='d']">
				<sizes>
					<xsl:for-each select="field[@id='215']/subfield[@id='a' or @id='d']">
						<size>
							<xsl:value-of select="." />
						</size>
					</xsl:for-each>
				</sizes>
			</xsl:if>

			<xsl:if test="field[@id='333']/subfield[@id='a']">
				<rightsList>
					<xsl:for-each select="field[@id='333']/subfield[@id='a']">
						<rights>
							<xsl:value-of select="." />
						</rights>
					</xsl:for-each>
				</rightsList>
			</xsl:if>

			<xsl:if test="field[@id='330']/subfield[@id='a']">
				<descriptions>
					<xsl:for-each select="field[@id='330']/subfield[@id='a']">
						<description descriptionType="Abstract">
							<xsl:value-of select="." />
						</description>
					</xsl:for-each>
				</descriptions>
			</xsl:if>

		</resource>
	</xsl:template>

	<xsl:template name="personalCreator">
		<creator>
			<xsl:call-template name="personalCreatorName" />
			<xsl:call-template name="givenName" />
			<xsl:call-template name="familyName" />
			<xsl:call-template name="nameId" />
			<xsl:for-each select="subfield[@id='p']">
				<affiliation>
					<xsl:value-of select="." />
				</affiliation>
			</xsl:for-each>
		</creator>
	</xsl:template>


	<xsl:template name="personalCreatorName">
		<creatorName>
			<xsl:value-of select="subfield[@id='a']" />
			<xsl:if test="subfield[@id='d']">
				<xsl:text> </xsl:text>
				<xsl:value-of select="subfield[@id='d']" />
			</xsl:if>
			<xsl:choose>
				<xsl:when test="subfield[@id='g']">
					<xsl:text>, </xsl:text>
					<xsl:value-of select="subfield[@id='g']" />
				</xsl:when>
				<xsl:otherwise>
					<xsl:if test="subfield[@id='b']">
						<xsl:text>, </xsl:text>
						<xsl:value-of select="subfield[@id='b']" />
					</xsl:if>
				</xsl:otherwise>
			</xsl:choose>
			<xsl:if test="subfield[@id='c']">
				<xsl:text> (</xsl:text>
				<xsl:for-each select="subfield[@id='c']">
					<xsl:value-of select="." />
					<xsl:if test="position() != last()">
						<xsl:text>;</xsl:text>
					</xsl:if>
				</xsl:for-each>
				<xsl:text>) </xsl:text>
			</xsl:if>
		</creatorName>
	</xsl:template>

	<xsl:template name="corporateCreator">
		<creator>
			<creatorName>
				<xsl:value-of select="subfield[@id='a']" />
				<xsl:for-each select="subfield[@id='b']">
					<xsl:text>. </xsl:text>
					<xsl:value-of select="." />
				</xsl:for-each>
			</creatorName>
		</creator>
	</xsl:template>

	<xsl:template name="givenName">
		<xsl:choose>
			<xsl:when test="indicator[@id='2']=0 and subfield[@id='a']">
				<givenName>
					<xsl:value-of select="subfield[@id='a']" />
				</givenName>
			</xsl:when>
			<xsl:when test="indicator[@id='2']=1 and subfield[@id='b' or @id='g']">
				<givenName>
					<xsl:choose>
						<xsl:when test="subfield[@id='g']">
							<xsl:choose>
								<xsl:when test="contains(subfield[@id='g'], ' ')">
									<xsl:value-of select="substring-before(subfield[@id='g'], ' ')" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="subfield[@id='g']" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:when>
						<xsl:otherwise>
							<xsl:choose>
								<xsl:when test="contains(subfield[@id='b'], ' ')">
									<xsl:value-of select="substring-before(subfield[@id='b'], ' ')" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="subfield[@id='b']" />
								</xsl:otherwise>
							</xsl:choose>
						</xsl:otherwise>
					</xsl:choose>
				</givenName>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="familyName">
		<xsl:if test="indicator[@id='2']=1 and subfield[@id='a']">
			<familyName>
				<xsl:value-of select="subfield[@id='a']" />
			</familyName>
		</xsl:if>
	</xsl:template>

	<xsl:template name="nameId">
		<xsl:choose>
			<xsl:when test="starts-with(subfield[@id='o'], 'isni')">
				<nameIdentifier nameIdentifierScheme="ISNI">
					<xsl:value-of select="substring-after(subfield[@id='o'], 'isni ')" />
				</nameIdentifier>
			</xsl:when>
			<xsl:when test="starts-with(subfield[@id='o'], 'orcid')">
				<nameIdentifier nameIdentifierScheme="ORCID">
					<xsl:value-of select="substring-after(subfield[@id='o'], 'orcid ')" />
				</nameIdentifier>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="getLanguage">
		<xsl:param name="src"
				   select="substring(field[@id='100']/subfield[@id='a'], 23, 3)" />
		<xsl:choose>
			<xsl:when test="$src='rus'">
				<xsl:text>ru</xsl:text>
			</xsl:when>
			<xsl:when test="$src='eng'">
				<xsl:text>en</xsl:text>
			</xsl:when>
			<xsl:when test="$src='ger'">
				<xsl:text>de</xsl:text>
			</xsl:when>
			<xsl:when test="$src='fra'">
				<xsl:text>fr</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$src" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="getContributorType">
		<xsl:param name="src" select="subfield[@id='4']" />
		<xsl:choose>
			<xsl:when test="$src='260' or src='587'">
				<xsl:text>RightsHolder</xsl:text>
			</xsl:when>
			<xsl:when test="$src='310'">
				<xsl:text>Distributor</xsl:text>
			</xsl:when>
			<xsl:when test="$src='340' or $src='651'">
				<xsl:text>Editor</xsl:text>
			</xsl:when>
			<xsl:when test="$src='400'">
				<xsl:text>Funder</xsl:text>
			</xsl:when>
			<xsl:when test="$src='595'">
				<xsl:text>ResearchGroup</xsl:text>
			</xsl:when>
			<xsl:when test="$src='630'">
				<xsl:text>Producer</xsl:text>
			</xsl:when>
			<xsl:when test="$src='637'">
				<xsl:text>ProjectManager</xsl:text>
			</xsl:when>
			<xsl:when test="$src='673'">
				<xsl:text>ProjectLeader</xsl:text>
			</xsl:when>
			<xsl:when test="$src='677'">
				<xsl:text>Researcher</xsl:text>
			</xsl:when>
			<xsl:when test="$src='723'">
				<xsl:text>Sponsor</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>Other</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="getResourceTypeGeneral">
		<xsl:param name="src" select="leader/type" />
		<xsl:choose>
			<xsl:when test="$src='a'">
				<xsl:text>Text</xsl:text>
			</xsl:when>
			<xsl:when test="$src='g'">
				<xsl:text>Audiovisual</xsl:text>
			</xsl:when>
			<xsl:when test="$src='i' or $src='j'">
				<xsl:text>Sound</xsl:text>
			</xsl:when>
			<xsl:when test="$src='k'">
				<xsl:text>Image</xsl:text>
			</xsl:when>
			<xsl:when test="$src='r'">
				<xsl:text>PhysicalObject</xsl:text>
			</xsl:when>
			<xsl:when
					test="$src='l' and starts-with(field[@id='135']/subfield[@id='a'], 'a')">
				<xsl:text>Dataset</xsl:text>
			</xsl:when>
			<xsl:when
					test="$src='l' and starts-with(field[@id='135']/subfield[@id='a'], 'b')">
				<xsl:text>Software</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>Other</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
