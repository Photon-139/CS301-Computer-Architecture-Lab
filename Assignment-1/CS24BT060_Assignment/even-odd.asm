	.data
a:
	11
	.text
main:
    load %x0, $a, %x3
    divi %x3, 2, %x4
    addi %x0, 1, %x5
    beq %x31, %x0, even
    beq %x31, %x5, odd
even:
    subi %x0, 1, %x10
    end
odd:
    addi %x0, 1, %x10
    end