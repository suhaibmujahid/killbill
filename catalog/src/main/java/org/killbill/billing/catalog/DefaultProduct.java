/*
 * Copyright 2010-2013 Ning, Inc.
 *
 * Ning licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.killbill.billing.catalog;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.killbill.billing.catalog.api.Limit;
import org.killbill.billing.catalog.api.Plan;
import org.killbill.billing.catalog.api.Product;
import org.killbill.billing.catalog.api.ProductCategory;
import org.killbill.xmlloader.ValidatingConfig;
import org.killbill.xmlloader.ValidationErrors;

@XmlAccessorType(XmlAccessType.NONE)
public class DefaultProduct extends ValidatingConfig<StandaloneCatalog> implements Product {

    private static final CatalogEntityCollection<DefaultProduct> EMPTY_PRODUCT_COLLECTION = new CatalogEntityCollection<DefaultProduct>();

    @XmlAttribute(required = true)
    @XmlID
    private String name;

    @XmlElement(required = true)
    private ProductCategory category;

    @XmlElementWrapper(name = "included", required = false)
    @XmlIDREF
    //@XmlElement(name = "addonProduct", required = false)
    @XmlElement(type=DefaultProduct.class, name = "addonProduct", required = false)
    //@XmlJavaTypeAdapter(DefaultProduct.Adapter.class)
    private CatalogEntityCollection<DefaultProduct> included;

    @XmlElementWrapper(name = "available", required = false)
    @XmlIDREF
    //@XmlElement(name = "addonProduct", required = false)
    @XmlElement(type=DefaultProduct.class, name = "addonProduct", required = false)
    //@XmlJavaTypeAdapter(DefaultProduct.Adapter.class)
    private CatalogEntityCollection<DefaultProduct> available;

    @XmlElementWrapper(name = "limits", required = false)
    @XmlElement(name = "limit", required = false)
    private DefaultLimit[] limits;

    //Not included in XML
    private String catalogName;

    @Override
    public String getCatalogName() {
        return catalogName;
    }

    @Override
    public ProductCategory getCategory() {
        return category;
    }



    @Override
    public Collection<Product> getIncluded() {
        return (Collection<Product>) included.getEntries();
    }

    @Override
    public Collection<Product> getAvailable() {
        return (Collection<Product>) available.getEntries();
    }

    public CatalogEntityCollection<DefaultProduct> getCollectionAvailable() {
        return available;
    }

    public DefaultProduct() {
        included = EMPTY_PRODUCT_COLLECTION;
        available = EMPTY_PRODUCT_COLLECTION;
        limits = new DefaultLimit[0];
    }

    public DefaultProduct(final String name, final ProductCategory category) {
        this.category = category;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public boolean isIncluded(final DefaultProduct addon) {
        for (final Product p : included) {
            if (addon == p) {
                return true;
            }
        }
        return false;
    }

    public boolean isAvailable(final DefaultProduct addon) {
        for (final Product p : included) {
            if (addon == p) {
                return true;
            }
        }
        return false;
    }

    @Override
    public DefaultLimit[] getLimits() {
        return limits;
    }
    
    
    protected Limit findLimit(String unit) {
        for(Limit limit: limits) {
            if(limit.getUnit().getName().equals(unit) ) {
                    return limit;
            }
        }
        return null;
    }
    
    @Override
    public boolean compliesWithLimits(String unit, double value) {
        Limit l = findLimit(unit);
        if (l == null) {
            return true;
        }
        return l.compliesWith(value);
    }


    
    @Override
    public void initialize(final StandaloneCatalog catalog, final URI sourceURI) {
        catalogName = catalog.getCatalogName();
    }

    @Override
    public ValidationErrors validate(final StandaloneCatalog catalog, final ValidationErrors errors) {
        //TODO: MDW validation: inclusion and exclusion lists can only contain addon products
        //TODO: MDW validation: a given product can only be in, at most, one of inclusion and exclusion lists
        return errors;
    }

    public DefaultProduct setName(final String name) {
        this.name = name;
        return this;
    }

    public DefaultProduct setCatagory(final ProductCategory category) {
        this.category = category;
        return this;
    }

    public DefaultProduct setCategory(final ProductCategory category) {
        this.category = category;
        return this;
    }

    public DefaultProduct setIncluded(final Collection<Product> included) {
        this.included = new CatalogEntityCollection<DefaultProduct>(included);
        return this;
    }

    public DefaultProduct setAvailable(final Collection<Product> available) {
        this.available = new CatalogEntityCollection<DefaultProduct>(available);
        return this;
    }

    public DefaultProduct setCatalogName(final String catalogName) {
        this.catalogName = catalogName;
        return this;
    }

    @Override
    public String toString() {
        return "DefaultProduct{" +
               "name='" + name + '\'' +
               ", category=" + category +
               ", included=" + included +
               ", available=" + available +
               ", limits=" + Arrays.toString(limits) +
               ", catalogName='" + catalogName + '\'' +
               '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DefaultProduct)) {
            return false;
        }

        final DefaultProduct product = (DefaultProduct) o;

        if (name != null ? !name.equals(product.name) : product.name != null) {
            return false;
        }
        if (category != product.category) {
            return false;
        }
        if (included != null ? !included.equals(product.included) : product.included != null) {
            return false;
        }
        if (available != null ? !available.equals(product.available) : product.available != null) {
            return false;
        }
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(limits, product.limits)) {
            return false;
        }
        return catalogName != null ? catalogName.equals(product.catalogName) : product.catalogName == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (included != null ? included.hashCode() : 0);
        result = 31 * result + (available != null ? available.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(limits);
        result = 31 * result + (catalogName != null ? catalogName.hashCode() : 0);
        return result;
    }
}
