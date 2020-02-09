<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes"/>
	<xsl:variable name="tableBorder" select="'solid'"/>
	<xsl:variable name="normalTextSize" select="'9pt'"/>
	<xsl:variable name="signatureBorder" select="'solid'"/>
	<xsl:variable name="imagePath" select="'$IMAGES_DIRECTORY$'"/>
	<xsl:variable name="lightGray">#CCCCCC</xsl:variable>

	<xsl:template match="/">
		<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
			
			<fo:layout-master-set>
				<fo:simple-page-master master-name="A4">
					<fo:region-body />
				</fo:simple-page-master>
			</fo:layout-master-set>
			
			<fo:page-sequence master-reference="A4">
				<fo:flow flow-name="xsl-region-body">
					<fo:block>Hello W3Schools</fo:block>
				</fo:flow>
			</fo:page-sequence>
			
		</fo:root>
	</xsl:template>

  <!--  Display account number -->
	<xsl:template match="account_no">
	   <xsl:value-of select="account_no"/>
	</xsl:template>
	
     <!-- Sales Invoice -->
	<xsl:template match="sales_order">
	      <fo:table-row>
	            <fo:table-cell>
				      <fo:block text-align="left" font-size="{$normalTextSize}">
					         <fo:inline font-weight="bold">
						     	<xsl:text>Invoice No:&#xA0;</xsl:text>
							 </fo:inline>
							 <xsl:value-of select="invoice_details/invoice_no"/>
					 </fo:block>
				</fo:table-cell>
		  </fo:table-row>	
	      <fo:table-row>
	            <fo:table-cell>
				      <fo:block text-align="left" font-size="{$normalTextSize}">
					  		<fo:inline font-weight="bold">
						    	 <xsl:text>Invoice Date:&#xA0;</xsl:text>
							 </fo:inline>
							 <xsl:value-of select="invoice_date"/>
					 </fo:block>
				</fo:table-cell>
		  </fo:table-row>	
  	      <fo:table-row>
	            <fo:table-cell>
				      <fo:block text-align="left">
						     <xsl:text>&#xA0;</xsl:text>
					 </fo:block>
				</fo:table-cell>
		  </fo:table-row>	
	</xsl:template>

	<!-- Company Template -->
	<xsl:template match="company">
		<fo:table-row>
			<fo:table-cell>
				<fo:block font-size="{$normalTextSize}" font-weight="bold">
				    <xsl:text>&#xA0;</xsl:text>
					<xsl:value-of select="name"/>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
		<fo:table-row>
			<fo:table-cell>
			     <xsl:if test="address1">
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:value-of select="address1"/>
						</fo:block>
                 </xsl:if>
			     <xsl:if test="address2">
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:value-of select="address2"/>
						</fo:block>
                 </xsl:if>
			     <xsl:if test="address3">
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:value-of select="address3"/>
						</fo:block>
                 </xsl:if>
			     <xsl:if test="address4">
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:value-of select="address4"/>
						</fo:block>
                 </xsl:if>
			</fo:table-cell>
		</fo:table-row>
		<fo:table-row>
			<fo:table-cell>
				<fo:block font-size="{$normalTextSize}">
				    <xsl:text>&#xA0;</xsl:text>
					<xsl:value-of select="city"/>
					<xsl:text>,&#xA0;</xsl:text>
					<xsl:value-of select="state"/>
					<xsl:text>&#xA0;</xsl:text>
					<xsl:value-of select="zip"/>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
		<xsl:if test="phone">
			<fo:table-row>
				<fo:table-cell>
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:text>Phone&#xA0;</xsl:text>
							<xsl:value-of select="phone"/>
						</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</xsl:if>
		<xsl:if test="fax">
			<fo:table-row>
				<fo:table-cell>
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:text>Fax &#xA0;</xsl:text>
							<xsl:value-of select="fax"/>
						</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</xsl:if>
		<xsl:if test="email">
			<fo:table-row>
				<fo:table-cell>
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:text>Email&#xA0;</xsl:text>
							<xsl:value-of select="email"/>
						</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</xsl:if>
		<xsl:if test="website">
			<fo:table-row>
				<fo:table-cell>
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:text>Website&#xA0;</xsl:text>
							<xsl:value-of select="website"/>
						</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</xsl:if>
	</xsl:template>

	<!--                                       -->
	<!-- Client Template         -->
	<!--                                       -->
	<xsl:template match="vw_business_address">
		<fo:table-row>
			<fo:table-cell>
				<fo:block font-size="{$normalTextSize}" font-weight="bold">
				    <xsl:text>&#xA0;</xsl:text>
					<xsl:value-of select="bus_longname"/>
				</fo:block>
			</fo:table-cell>
		</fo:table-row>
		<fo:table-row>
			<fo:table-cell>
			     <xsl:if test="addr1 and string-length(addr1) &gt; 0">
							<fo:block font-size="{$normalTextSize}">
							    <xsl:text>&#xA0;</xsl:text>
								<xsl:value-of select="addr1"/>
							</fo:block>
           </xsl:if>
			     <xsl:if test="addr2 and string-length(addr2) &gt; 0">
							<fo:block font-size="{$normalTextSize}">
							    <xsl:text>&#xA0;</xsl:text>
								<xsl:value-of select="addr2"/>
							</fo:block>
           </xsl:if>
				<xsl:if test="addr3 and string-length(addr3) &gt; 0">
							<fo:block font-size="{$normalTextSize}">
							  <xsl:text>&#xA0;</xsl:text>
								<xsl:value-of select="addr3"/>
							</fo:block>
           </xsl:if>
				<xsl:if test="addr4 and string-length(addr4) &gt; 0">
							<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
								<xsl:value-of select="addr4"/>
							</fo:block>
           </xsl:if>
           <xsl:if test="string-length(addr1) &lt;= 0 and string-length(addr2) &lt;= 0 and string-length(addr3) &lt;= 0 and string-length(addr4) &lt;= 0">
							<fo:block font-size="{$normalTextSize}">
	               <xsl:text>&#032;</xsl:text>
							</fo:block>
           </xsl:if>

			</fo:table-cell>
		</fo:table-row>
    <xsl:if test="string-length(zip_city) &gt; 0 and string-length(zip_state) &gt; 0 and (string-length(addr_zip) &gt; 0 and addr_zip != 0)">
				<fo:table-row>
				    <fo:table-cell>
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:value-of select="zip_city"/>
							<xsl:text>,&#xA0;</xsl:text>
							<xsl:value-of select="zip_state"/>
							<xsl:text>&#xA0;</xsl:text>
							<xsl:value-of select="addr_zip"/>
						</fo:block>
			        </fo:table-cell>	
				</fo:table-row>
		</xsl:if>
		<xsl:if test="phone">
			<fo:table-row>
				<fo:table-cell>
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:text>Phone&#xA0;</xsl:text>
							<xsl:value-of select="addr_phone_main"/>
						</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</xsl:if>
		<xsl:if test="fax">
			<fo:table-row>
				<fo:table-cell>
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:text>Fax &#xA0;</xsl:text>
							<xsl:value-of select="addr_phone_fax"/>
						</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</xsl:if>
		<xsl:if test="email">
			<fo:table-row>
				<fo:table-cell>
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:text>Email&#xA0;</xsl:text>
							<xsl:value-of select="email"/>
						</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</xsl:if>
		<xsl:if test="website">
			<fo:table-row>
				<fo:table-cell>
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:text>Website&#xA0;</xsl:text>
							<xsl:value-of select="bus_website"/>
						</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</xsl:if>
	</xsl:template>

    <!-- Customer's Address -->
	<xsl:template match="address">
		<fo:table-row>
			<fo:table-cell>
			     <xsl:if test="address1">
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:value-of select="address1"/>
						</fo:block>
                 </xsl:if>
			     <xsl:if test="address2">
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:value-of select="address2"/>
						</fo:block>
                 </xsl:if>
			     <xsl:if test="address3">
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:value-of select="address3"/>
						</fo:block>
                 </xsl:if>
			     <xsl:if test="address4">
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:value-of select="address4"/>
						</fo:block>
                 </xsl:if>
			</fo:table-cell>
		</fo:table-row>
		<fo:table-row>
		    <xsl:apply-templates select="zipcode"/>
		</fo:table-row>
		<xsl:if test="phone">
			<fo:table-row>
				<fo:table-cell>
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:text>Phone&#xA0;</xsl:text>
							<xsl:value-of select="phone"/>
						</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</xsl:if>
		<xsl:if test="fax">
			<fo:table-row>
				<fo:table-cell>
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:text>Fax &#xA0;</xsl:text>
							<xsl:value-of select="fax"/>
						</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</xsl:if>
		<xsl:if test="email">
			<fo:table-row>
				<fo:table-cell>
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:text>Email&#xA0;</xsl:text>
							<xsl:value-of select="email"/>
						</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</xsl:if>
		<xsl:if test="website">
			<fo:table-row>
				<fo:table-cell>
						<fo:block font-size="{$normalTextSize}">
						    <xsl:text>&#xA0;</xsl:text>
							<xsl:text>Website&#xA0;</xsl:text>
							<xsl:value-of select="website"/>
						</fo:block>
				</fo:table-cell>
			</fo:table-row>
		</xsl:if>	
	</xsl:template>

    <!-- Sales Order Items -->
	<xsl:template match="salesorder_items">
	      <fo:table-row>
	        <fo:table-cell border-right-color="black" border-right-width=".5pt" border-right-style="solid">
			     <fo:block text-align="left" font-size="{$normalTextSize}">
				     <xsl:text>&#xA0;</xsl:text>
				 	   <xsl:value-of select="item_id"/>
				   </fo:block>
			    </fo:table-cell>
	        <fo:table-cell border-right-color="black" border-right-width=".5pt" border-right-style="solid">
			     <fo:block text-align="left" font-size="{$normalTextSize}">
				 	 <xsl:text>&#xA0;</xsl:text>
				     <xsl:value-of select="item_name"/>
			     	<xsl:if test="item_name_override and string-length(item_name_override) &gt; 0">
					   <xsl:text>&#xA0;-&#xA0;</xsl:text>
					   <xsl:value-of select="item_name_override"/>
					 </xsl:if>
				   </fo:block>
			    </fo:table-cell>
	        <fo:table-cell border-right-color="black" border-right-width=".5pt" border-right-style="solid">
			     <fo:block text-align="center" font-size="{$normalTextSize}">
				     <xsl:value-of select="format-number(order_qty, '#,##0.##')"/>
				   </fo:block>
			    </fo:table-cell>
	        <fo:table-cell border-right-color="black" border-right-width=".5pt" border-right-style="solid">
			     <fo:block text-align="right" font-size="{$normalTextSize}">
				     <xsl:value-of select="format-number(retail_price, '#,##0.00')"/>
				   </fo:block>
			    </fo:table-cell>
	        <fo:table-cell border-right-color="black" border-right-width=".5pt" border-right-style="solid">
			     <fo:block text-align="right" font-size="{$normalTextSize}">
				     <xsl:value-of select="format-number(invoice_amount, '#,##0.00')"/>
				   </fo:block>
			    </fo:table-cell>
		  </fo:table-row>
	</xsl:template>

     <!-- Sales Order Total Line -->
	<xsl:template match="xact">
	<fo:table width="100%" table-layout="fixed">
	    <fo:table-column column-width="10%"/>
		<fo:table-column column-width="50%"/>
		<fo:table-column column-width="10%"/>
		<fo:table-column column-width="15%"/>
		<fo:table-column column-width="15%"/>
		<fo:table-body>
	      <fo:table-row>
	        <fo:table-cell>
			     <fo:block>
				 	 <xsl:text>&#xA0;</xsl:text>
				 </fo:block>
			</fo:table-cell>
	        <fo:table-cell>
			     <fo:block>
				 	 <xsl:text>&#xA0;</xsl:text>
				 </fo:block>
			</fo:table-cell>
			<fo:table-cell>
			     <fo:block>
				 	 <xsl:text>&#xA0;</xsl:text>
				 </fo:block>
			</fo:table-cell>
	        <fo:table-cell>
			     <fo:block text-align="center" font-size="11pt">
				     <xsl:text>TOTAL</xsl:text>
				 </fo:block>
			</fo:table-cell>
	        <fo:table-cell border-color="black" border-width=".5pt" border-style="solid">
			     <fo:block text-align="right" font-size="{$normalTextSize}" font-weight="bold">
				     <xsl:value-of select="format-number(xact_amount, '$#,##0.00')"/>
				 </fo:block>
			</fo:table-cell>
		  </fo:table-row>
		</fo:table-body>
	</fo:table>
</xsl:template>

<!-- Print Zipcode related data -->
<xsl:template match="zipcode">
	<fo:table-cell>
		<fo:block font-size="{$normalTextSize}">
		    <xsl:text>&#xA0;</xsl:text>
			<xsl:value-of select="city"/>
			<xsl:text>,&#xA0;</xsl:text>
			<xsl:value-of select="state"/>
			<xsl:text>&#xA0;</xsl:text>
			<xsl:value-of select="zip"/>
		</fo:block>
	</fo:table-cell>	
</xsl:template>

</xsl:stylesheet>