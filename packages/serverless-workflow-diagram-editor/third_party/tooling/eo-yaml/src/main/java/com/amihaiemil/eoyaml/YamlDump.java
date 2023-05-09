/**
 * Copyright (c) 2016-2020, Mihai Emil Andronache
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package com.amihaiemil.eoyaml;

/**
 * In YAML, "dumping" means representing the state of an Object as YAML.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id: 31e32467ccd9f865ee17cbc4342df72a114f938e $
 * @since 4.3.3
 */
public interface YamlDump {

    /**
     *
     * Dump an Object, represent it as YAML.
     * Generally, if the Object is a Collection or on Array, the resulting
     * YamlNode will be a YamlSequence.<br><br>
     * If the Object is a Map or other kind of Object, the resulting
     * YamlNode will be a YamlSequence.<br><br>
     * If the Object is a String, LocalDate, LocaDateTime or a primitive,
     * the resulting YamlNode will be a plain Scalar.
     * @return YAML Representation.
     */
    YamlNode dump();

    /**
     * Convenience method, equivalent to calling the
     * dump(...) method and casting the YamlNode to YamlMapping.
     * @return YamlMapping.
     */
    default YamlMapping dumpMapping() {
        return (YamlMapping) this.dump();
    }

    /**
     * Convenience method, equivalent to calling the
     * dump(...) method and casting the YamlNode to YamlSequence.
     * @return YamlSequence.
     */
    default YamlSequence dumpSequence() {
        return (YamlSequence) this.dump();
    }

    /**
     * Convenience method, equivalent to calling the
     * dump(...) method and casting the YamlNode to Scalar.
     * @return Scalar.
     */
    default Scalar dumpScalar() {
        return (Scalar) this.dump();
    }

}
