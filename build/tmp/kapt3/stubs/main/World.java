
import java.lang.System;

@kotlin.Metadata(mv = {1, 4, 0}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\r\b\u0007\u0018\u0000 -2\u00020\u0001:\u0002,-BY\b\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u000e\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010\u0005\u0012\u000e\u0010\u0007\u001a\n\u0012\u0004\u0012\u00020\b\u0018\u00010\u0005\u0012\u000e\u0010\t\u001a\n\u0012\u0004\u0012\u00020\n\u0018\u00010\u0005\u0012\u000e\u0010\u000b\u001a\n\u0012\u0004\u0012\u00020\f\u0018\u00010\u0005\u0012\b\u0010\r\u001a\u0004\u0018\u00010\u000e\u00a2\u0006\u0002\u0010\u000fB\u0005\u00a2\u0006\u0002\u0010\u0010J\u0014\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00130\u00122\u0006\u0010\u0014\u001a\u00020\nJ\u0014\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00130\u00122\u0006\u0010\u0016\u001a\u00020\u0006J\u0014\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00130\u00122\u0006\u0010\u0018\u001a\u00020\fJ\u0014\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00130\u00122\u0006\u0010\u001a\u001a\u00020\bJ\u001c\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\n0\u00122\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001fJ\f\u0010 \u001a\b\u0012\u0004\u0012\u00020\n0!J\u0014\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u00060\u00122\u0006\u0010#\u001a\u00020\u0013J\f\u0010$\u001a\b\u0012\u0004\u0012\u00020\u00060!J\f\u0010%\u001a\b\u0012\u0004\u0012\u00020\f0!J\u0014\u0010&\u001a\b\u0012\u0004\u0012\u00020\b0\u00122\u0006\u0010#\u001a\u00020\u0013J\f\u0010\'\u001a\b\u0012\u0004\u0012\u00020\b0!J\u0014\u0010(\u001a\b\u0012\u0004\u0012\u00020\u00130\u00122\u0006\u0010\u0014\u001a\u00020\nJ\u0014\u0010)\u001a\b\u0012\u0004\u0012\u00020\u00130\u00122\u0006\u0010\u0016\u001a\u00020\u0006J\u0014\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00130\u00122\u0006\u0010\u0018\u001a\u00020\fJ\u0014\u0010+\u001a\b\u0012\u0004\u0012\u00020\u00130\u00122\u0006\u0010\u001a\u001a\u00020\bR\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006."}, d2 = {"LWorld;", "", "seen1", "", "nations", "", "LNation;", "provinces", "LProvince;", "groups", "LGroup;", "orders", "LOrder;", "serializationConstructorMarker", "Lkotlinx/serialization/internal/SerializationConstructorMarker;", "(ILjava/util/List;Ljava/util/List;Ljava/util/List;Ljava/util/List;Lkotlinx/serialization/internal/SerializationConstructorMarker;)V", "()V", "addGroup", "Larrow/core/Option;", "", "group", "addNation", "nation", "addOrder", "order", "addProvince", "province", "getGroup", "location", "LLocation;", "type", "LGroup$Type;", "getGroups", "", "getNation", "name", "getNations", "getOrders", "getProvince", "getProvinces", "removeGroup", "removeNation", "removeOrder", "removeProvince", "$serializer", "Companion", "Diplomacy"})
@kotlinx.serialization.Serializable
public final class World {
    private final java.util.List<Nation> nations = null;
    private final java.util.List<Province> provinces = null;
    private final java.util.List<Group> groups = null;
    private final java.util.List<Order> orders = null;
    public static final World.Companion Companion = null;
    
    @org.jetbrains.annotations.NotNull
    public final arrow.core.Option<Nation> getNation(@org.jetbrains.annotations.NotNull
    java.lang.String name) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final arrow.core.Option<Province> getProvince(@org.jetbrains.annotations.NotNull
    java.lang.String name) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final arrow.core.Option<Group> getGroup(@org.jetbrains.annotations.NotNull
    Location location, @org.jetbrains.annotations.NotNull
    Group.Type type) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<Nation> getNations() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<Province> getProvinces() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<Group> getGroups() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<Order> getOrders() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final arrow.core.Option<java.lang.String> addNation(@org.jetbrains.annotations.NotNull
    Nation nation) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final arrow.core.Option<java.lang.String> addProvince(@org.jetbrains.annotations.NotNull
    Province province) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final arrow.core.Option<java.lang.String> addGroup(@org.jetbrains.annotations.NotNull
    Group group) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final arrow.core.Option<java.lang.String> addOrder(@org.jetbrains.annotations.NotNull
    Order order) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final arrow.core.Option<java.lang.String> removeNation(@org.jetbrains.annotations.NotNull
    Nation nation) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final arrow.core.Option<java.lang.String> removeProvince(@org.jetbrains.annotations.NotNull
    Province province) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final arrow.core.Option<java.lang.String> removeGroup(@org.jetbrains.annotations.NotNull
    Group group) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final arrow.core.Option<java.lang.String> removeOrder(@org.jetbrains.annotations.NotNull
    Order order) {
        return null;
    }
    
    public World() {
        super();
    }
    
    public static final void write$Self(@org.jetbrains.annotations.NotNull
    World self, @org.jetbrains.annotations.NotNull
    kotlinx.serialization.encoding.CompositeEncoder output, @org.jetbrains.annotations.NotNull
    kotlinx.serialization.descriptors.SerialDescriptor serialDesc) {
    }
    
    @kotlin.Metadata(mv = {1, 4, 0}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004H\u00c6\u0001\u00a8\u0006\u0006"}, d2 = {"LWorld$Companion;", "", "()V", "serializer", "Lkotlinx/serialization/KSerializer;", "LWorld;", "Diplomacy"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        public final kotlinx.serialization.KSerializer<World> serializer() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {1, 4, 0}, bv = {1, 0, 3}, k = 1, d1 = {"\u00006\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0003J\u0018\u0010\b\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\n0\tH\u00d6\u0001\u00a2\u0006\u0002\u0010\u000bJ\u0011\u0010\f\u001a\u00020\u00022\u0006\u0010\r\u001a\u00020\u000eH\u00d6\u0001J\u0019\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0002H\u00d6\u0001R\u0014\u0010\u0004\u001a\u00020\u00058VX\u00d6\u0005\u00a2\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0014"}, d2 = {"World.$serializer", "Lkotlinx/serialization/internal/GeneratedSerializer;", "LWorld;", "()V", "descriptor", "Lkotlinx/serialization/descriptors/SerialDescriptor;", "getDescriptor", "()Lkotlinx/serialization/descriptors/SerialDescriptor;", "childSerializers", "", "Lkotlinx/serialization/KSerializer;", "()[Lkotlinx/serialization/KSerializer;", "deserialize", "decoder", "Lkotlinx/serialization/encoding/Decoder;", "serialize", "", "encoder", "Lkotlinx/serialization/encoding/Encoder;", "value", "Diplomacy"})
    @java.lang.Deprecated
    public static final class $serializer implements kotlinx.serialization.internal.GeneratedSerializer<World> {
        public static final World.$serializer INSTANCE = null;
        
        private $serializer() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull
        @java.lang.Override
        @java.lang.Deprecated
        public World patch(@org.jetbrains.annotations.NotNull
        kotlinx.serialization.encoding.Decoder decoder, @org.jetbrains.annotations.NotNull
        World old) {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        public kotlinx.serialization.KSerializer<?>[] typeParametersSerializers() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        @java.lang.Override
        public kotlinx.serialization.descriptors.SerialDescriptor getDescriptor() {
            return null;
        }
        
        @java.lang.Override
        public void serialize(@org.jetbrains.annotations.NotNull
        kotlinx.serialization.encoding.Encoder encoder, @org.jetbrains.annotations.NotNull
        World value) {
        }
        
        @org.jetbrains.annotations.NotNull
        @java.lang.Override
        public World deserialize(@org.jetbrains.annotations.NotNull
        kotlinx.serialization.encoding.Decoder decoder) {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull
        @java.lang.Override
        public kotlinx.serialization.KSerializer<?>[] childSerializers() {
            return null;
        }
    }
}