:: # Copyright 2013, Big Switch Networks, Inc.
:: #
:: # LoxiGen is licensed under the Eclipse Public License, version 1.0 (EPL), with
:: # the following special exception:
:: #
:: # LOXI Exception
:: #
:: # As a special exception to the terms of the EPL, you may distribute libraries
:: # generated by LoxiGen (LoxiGen Libraries) under the terms of your choice, provided
:: # that copyright and licensing notices generated by LoxiGen are not altered or removed
:: # from the LoxiGen Libraries and the notice provided below is (i) included in
:: # the LoxiGen Libraries, if distributed in source code form and (ii) included in any
:: # documentation for the LoxiGen Libraries, if distributed in binary form.
:: #
:: # Notice: "Copyright 2013, Big Switch Networks, Inc. This library was generated by the LoxiGen Compiler."
:: #
:: # You may not use this file except in compliance with the EPL or LOXI Exception. You may obtain
:: # a copy of the EPL at:
:: #
:: # http://www.eclipse.org/legal/epl-v10.html
:: #
:: # Unless required by applicable law or agreed to in writing, software
:: # distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
:: # WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
:: # EPL for the specific language governing permissions and limitations
:: # under the EPL.
::
:: # TODO coalesce format strings
:: from loxi_ir import *
:: from py_gen.oftype import gen_pack_expr
:: length_member = None
:: length_member_index = None
:: field_length_members = {}
:: field_length_indexes = {}
:: index = 0
:: for m in ofclass.members:
::     if type(m) == OFLengthMember:
::         length_member = m
::         length_member_index = index
        packed.append(${gen_pack_expr(m.oftype, '0', version=version)}) # placeholder for ${m.name} at index ${index}
::     elif type(m) == OFFieldLengthMember:
::         field_length_members[m.field_name] = m
::         field_length_indexes[m.field_name] = index
        packed.append(${gen_pack_expr(m.oftype, '0', version=version)}) # placeholder for ${m.name} at index ${index}
::     elif type(m) == OFPadMember:
        packed.append('\x00' * ${m.length})
::     elif type(m) == OFVarPadMember:
	packed.append( (length + ( ${m.pad_length} - 1 ) ) / ${m.pad_length} * ${m.pad_length} - length)
::     else:
        packed.append(${gen_pack_expr(m.oftype, 'self.' + m.name, version=version)})
::         if m.name in field_length_members:
::             field_length_member = field_length_members[m.name]
::             field_length_index = field_length_indexes[m.name]
        packed[${field_length_index}] = ${gen_pack_expr(field_length_member.oftype, 'len(packed[-1])', version=version)}
::         #endif
::     #endif
::     index += 1
:: #endfor
:: if length_member_index != None:
        length = sum([len(x) for x in packed])
:: if ofclass.has_internal_alignment:
        packed.append(loxi.generic_util.pad_to(8, length))
        length += len(packed[-1])
:: #endif
        packed[${length_member_index}] = ${gen_pack_expr(length_member.oftype, 'length', version=version)}
:: #endif
:: if ofclass.has_external_alignment:
        packed.append(loxi.generic_util.pad_to(8, length))
:: #endif
