package org.skyscreamer.yoga.selector;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.skyscreamer.yoga.configuration.EntityConfigurationRegistry;
import org.skyscreamer.yoga.configuration.YogaEntityConfiguration;

@SuppressWarnings("rawtypes")
public class FieldSelector implements Selector
{
    protected Map<String, FieldSelector> subSelectors = new HashMap<String, FieldSelector>();
    protected EntityConfigurationRegistry _entityConfigurationRegistry;
    private HashMap<String, Property> allFields;


    public FieldSelector( EntityConfigurationRegistry entityConfigurationRegistry)
    {
        _entityConfigurationRegistry = entityConfigurationRegistry;
    }

    @Override
    public FieldSelector getChildSelector( Class<?> instanceType, String fieldName )
    {
        return getSelector( fieldName );
    }

    public FieldSelector getSelector( String fieldName )
    {
        return subSelectors.get( fieldName );
    }

    @Override
    public boolean containsField( Class<?> instanceType, String property )
    {
        return containsField( property );
    }

    @Override
    public <T> Property<T> getProperty(Class<T> instanceType, String fieldName)
    {
        return getAllPossibleFieldMap( instanceType ).get( fieldName );
    }
    
    public boolean containsField( String property )
    {
        return subSelectors.containsKey( property );
    }

    public int getFieldCount()
    {
        return subSelectors.size();
    }

    public void register( String fieldName, FieldSelector subSelector )
    {
        subSelectors.put( fieldName, subSelector );
    }


    @Override
    public <T> Collection<Property<T>> getSelectedFields( Class<T> instanceType )
    {
        return getAllPossibleFieldMap( instanceType ).values();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Map<String, Property<T>> getAllPossibleFieldMap(Class<T> instanceType)
    {
        if(allFields == null)
        {
            // The assumption is that this case will only be called with 1 consistent instance type... which is why we can cache allFields
            YogaEntityConfiguration<T> entityConfiguration = _entityConfigurationRegistry.getEntityConfiguration( instanceType );
            Collection<String> selectableFields = entityConfiguration == null ? null : entityConfiguration.getSelectableFields();
            allFields = new HashMap<String, Property>();
            for (String name : subSelectors.keySet())
            {
                if ( selectableFields == null || selectableFields.contains(name) ) 
                {
                    allFields.put( name, new NamedProperty<T>( name ) );
                }
            }
        }
        return (HashMap) allFields;
    }

    @Override
    public boolean isInfluencedExternally()
    {
        return true;
    }

}
