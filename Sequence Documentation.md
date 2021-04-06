Created by Keith Meyer

# Sequence

Sequence is a tape/cell-based language that is fully Turing-complete (and a Turing tarpit). It is inspired by Brainf\*ck, and uses a similar command set, but uses only two symbols: the period and the space. All other characters are ignored and treated as comments. Unlike other languages that execute commands discretely, each command in Sequence depends upon the commands executed before it. Memory in Sequence is represented by an &quot;infinite&quot; tape, an sufficiently large array of bytes (at least 2E18 bytes) that are each initialized to zero. Memory is accessed using a data pointer (tape head) that starts at the zeroth byte and can move in either direction (to negative or positive positions).

## Syntax

In Sequence, code is executed in space-separated blocks, with each block containing a single command. Within each coding block, a command is represented by a sequence of dots, with each &quot;dot&quot; executing a different command based on its ordinal number. An example code block would be `...` which would execute the single dot command, the second dot command, and the third dot command, in order.

Command definitions are listed below.

| **Dot Representation** | **Ordinal Numbers** | **Command** | **Effective Command** |
| --- | --- | --- | --- |
| `.` | `1` | Increment the byte under the data pointer. | Increment the byte under the data pointer. |
| `..` | `2` | Decrement the byte under the data pointer. | \<none\> |
| `...` | `3` | Move data pointer right. | Move data pointer right. |
| `....` | `4` | Move data pointer left. | \<none\> |

While each ordinal number has a command, the execution of the commands before it often counteract the result from changing anything, resulting in an effective command of null. For example, two dots ( `..` ) will first increment the byte before decrementing the same byte, leaving the byte the same as it was before. Command blocks greater than four dots are executed according to modular arithmetic, ex: seven dots ( `......` ) would execute ordinals 1-4, and then 1-3 again, for an effective command of moving the data pointer right one.

In addition to command blocks, each group of two command blocks are themselves executed as block-based commands. For example, the sequence `1 3 2` ( `. ... ..` ) includes two block groupings, `1 3` and `3 2`.

Block-based commands are listed below:

| **Dot Representation\*** | **Ordinal Number\*** | **Command** | **Effective Command** |
| --- | --- | --- | --- |
| `x ` | `X 0` | No command (spacer) | Executes X. |
| `x .` | `X 1` | Repeats command block X. | Executes X, increments pointer, repeats X. |
| `x ..` | `X 2` | Repeats only last command of X. | Executes X, repeats only last command of X. |
| `. ...` | `1 3` | Copies byte from the left. | Increments pointer, moves pointer right, then copies. |
| `. ....` | `1 4` | Copies byte from right. | Increments pointer, copies byte from right. |
| `.. ...` | `2 3` | Begins loop if byte is nonzero, skips to matching 2 4 if zero. | Moves pointer right, begins loop if nonzero. |
| `.. ....` | `2 4` | Continues loop if byte is nonzero, ends loop if zero. | Continues loop if byte is nonzero, ends loop if zero. |
| `... ...` | `3 3` | Adds byte under pointer by the byte to the left. | Moves pointer right twice, adds by the left. |
| `... ....` | `3 4` | Adds byte under pointer by the byte to the right. | Moves pointer right , adds by right. |
| `.... ...` | `4 3` | Reads one byte of data and stores it under the pointer. | Moves pointer right, reads and stores byte. |
| `.... ....` | `4 4` | Outputs byte of data under the pointer. | Outputs byte of data under the pointer. |
| `x y` | `X Y` | Repeats only last command of X for Y times. (Y \> 4) | Executes X and Y, repeats X for Y times. |

_\*The variables_ _**x**_ _and_ _**y**_ _are used to represent an arbitrary code block._

It is important to note that commands within blocks are executed before block commands, resulting in more complicated effective commands. Also, block commands can use the same blocks as other block commands. Consider `1 3 2` ( `. ... ..` ), which will execute the first block ( `.` ), then the second block ( `...` ), and then the block command ( `. ...` ) before moving to the third block ( `..` ) and the next block command ( `... ..` ). This will will increment the byte under the pointer, move the pointer right, copy the pointer from the left, and then move the pointer right again.

Command blocks larger than 4 still use modular arithmetic within the block and _when on the left_, but not when being read as a block command _on the right_. This means something like `1 7 2` ( `. ....... ...` ), will first increment the byte under the pointer, move the pointer to the right, increment the new byte seven times, move the pointer right, and then add the byte on the left to the byte under the pointer. The opportunity to chain commands can greatly improve the code density if designed properly (such a process, however, is difficult and unwieldy, thus the tarpit designation).

## Sample Programs

An included sample program is a translated version of Brainf\*ck&#39;s &quot;Hello World!&quot; program into Sequence. In ordinal form this appears as:

```
1 8 0 2 2 0 4 2 3 0 3 0 1 6 2 2 2 0 4 2 3 0 3 0 1 2 0 3 0 1 1 0 3 0 1 1 0 3 0 1 0 4 6 0 3 2 2 0 2 4 0 3 0 1 0 3 0 1 0 3 0 2 2 0 3 2 1 0 4 2 3 0 4 2 4 0 4 2 2 0 2 4 0 3 2 0 4 4 0 3 0 2 2 2 2 0 4 4 0 1 5 0 4 4 4 0 1 2 0 1 0 4 4 0 3 2 0 4 4 0 4 2 2 0 4 4 0 4 2 0 4 4 0 1 2 0 1 0 4 4 0 2 6 0 4 4 0 2 8 4 0 3 2 0 1 0 4 4 0 3 0 1 2 0 4 4 0
```

Which in turn expands to:

```
. ........ .. .. .... .. ... ... . ...... .. .. .. .... .. ... ... . .. ... . . ... . . ... . .... ...... ... .. .. .. .... ... . ... . ... .. .. ... .. . .... .. ... .... .. .... .... .. .. .. .... ... .. .... .... ... .. .. .. .. .... .... . ..... .... .... .... . .. . .... .... ... .. .... .... .... .. .. .... .... .... .. .... .... . .. . .... .... .. ...... .... .... .. ........ .... ... .. . .... .... ... . .. .... ....
```

Of note is the fact that the original code does not use the language or chaining to its advantage, as the code is structured in such a way that chaining in fact hampers the flow and makes it longer than necessary. Thus this example is only included to help demonstrate that all the faculties of Brainf\*ck are supported in Sequence.

## Turing Completeness

The following is a Universal Turing machine based on BrainF\*ck code written by Daniel Cristofori and one of the Turing machine definitions provided by Yurii Rogozhin in &quot;Small universal Turing machines&quot; in Theoretical Computer Science. This specific Turing-machine simulates a Turing-complete class of tag-systems (a computational system by Emil Post). As Sequence can implement a Turing machine to simulate the tag system, which is Turing-complete, then it can simulate other Turing machines in turn. The exact functionality of the tag system is better described elsewhere, and is not explained here for brevity (more information can be found in the work by [Daniel B Cristofani)](http://www.hevanet.com/cristofd/brainfuck/utm.b)

```
1 1 0 3 0 1 2 0 3 2 0 3 0 1 0 4 2 3 0 3 2 0 4 2 0 4 3 0 4 2 3 0 3 0 1 6 2 2 0 4 2 0 4 2 3 0 4 2 3 0 2 2 0 3 0 2 4 0 4 2 0 4 2 0 2 4 0 4 2 0 4 2 3 0 3 0 2 4 0 3 0 2 4 0 3 0 2 2 0 4 2 3 0 4 2 0 4 2 0 1 6 2 2 0 3 2 0 2 2 0 4 2 3 0 4 2 0 4 2 2 0 2 2 2 2 0 3 2 0 2 2 0 4 2 3 0 2 2 0 3 0 2 4 0 4 2 0 2 4 0 2 4 0 4 2 0 4 2 3 0 4 2 2 0 4 2 0 4 2 3 0 4 2 0 2 4 0 1 0 4 2 1 0 4 2 3 0 3 0 2 4 0 4 2 0 4 2 0 1 0 3 0 2 2 0 3 2 0 3 0 2 4 0 4 2 0 2 4 0 4 2 0 4 2 3 0 4 2 4 0 3 0 4 2 3 0 2 2 0 4 2 3 0 3 0 1 5 0 2 2 0 4 2 2 0 2 4 0 3 0 4 2 3 0 4 2 1 0 3 0 2 2 4 0 1 0 4 2 0 4 2 0 4 2 0 1 1 0 3 1 0 4 2 3 0 2 2 0 4 2 3 0 4 2 0 4 2 0 1 0 3 0 2 2 0 3 0 2 2 0 4 2 3 0 4 2 0 4 2 0 4 2 3 0 2 2 4 0 3 2 2 0 4 2 3 0 4 2 0 4 2 1 2 0 3 1 0 2 2 0 4 2 3 0 4 2 0 4 2 0 2 2 2 0 3 0 2 2 3 2 3 0 1 1 0 4 2 2 0 4 2 3 0 4 2 0 4 2 0 1 0 3 1 0 3 0 2 2 0 4 2 2 0 4 2 3 0 4 2 0 4 2 2 0 3 0 2 2 0 3 0 2 2 0 4 2 3 0 4 2 0 4 2 0 1 1 2 0 3 1 0 3 0 1 4 0 2 2 0 4 2 3 0 3 0 2 2 0 4 2 2 0 4 2 3 0 4 2 0 4 2 2 0 3 0 2 2 0 3 2 2 0 4 2 3 0 4 2 0 4 2 2 0 3 2 2 0 4 2 3 0 4 2 0 4 2 0 1 1 0 3 0 3 2 2 0 4 2 2 0 4 2 3 0 4 2 0 4 2 5 0 3 2 0 3 0 1 2 0 4 2 2 0 4 2 3 0 4 2 0 4 2 1 2 0 3 2 0 3 0 1 0 4 2 2 0 4 2 3 0 3 0 4 2 3 0 2 2 4 0 4 2 2 0 4 2 3 0 4 2 0 4 2 2 0 3 2 0 3 0 1 1 0 4 2 2 0 4 2 3 0 4 2 0 4 2 2 0 3 0 3 2 2 2 0 4 2 2 0 4 2 3 0 4 2 0 4 2 0 1 1 2 0 3 1 0 3 0 1 0 4 2 2 0 4 2 3 0 4 2 0 4 2 0 4 2 3 0 2 2 0 2 4 0 3 0 2 2 0 3 2 0 1 2 0 4 2 0 4 2 3 0 4 2 0 4 2 1 6 2 2 0 3 1 0 3 0 2 2 2 0 4 2 2 0 4 2 3 0 4 2 2 0 3 2 0 1 2 0 4 2 0 4 2 3 0 4 2 4 2 2 0 3 2 2 0 2 4 0 2 4 0 2 4 0 2 4 0 2 4 0 2 4 0 2 4 0 2 4 0 2 4 0 2 4 0 2 4 0 2 4 0 2 4 0 2 4 0 2 4 0 2 4 0 2 4 0 2 4 0 2 4 0 2 4 0 2 4 0 2 4 0 4 2 0 4 2 3 0 2 2 0 3 2 0 4 2 3 0 4 2 0 4 2 0 1 0 3 2 2 0 2 4 0 4 2 0 4 2 0 4 2 0 4 2 3 0 3 2 0 3 0 1 0 4 2 0 4 2 0 4 2 2 0 2 4 0 2 4 0 4 2 3 0 3 2 0 3 0 1 0 4 2 0 4 2 0 4 2 2 4 0 2 4 0 3 2 4 0 3 0 4 2 3 0 2 2 0 4 2 3 0 2 2 2 2 0 4 2 3 0 2 2 0 4 2 0 2 4 0 2 4 0 3 0 2 4 0 3 0 4 2 3 0 1 1 0 4 2 3 0 4 2 0 1 6 2 2 0 3 0 2 2 2 0 2 4 0 3 0 2 4 0 1 0 4 2 0 1 2 0 4 2 3 0 4 2 3 0 3 0 1 6 2 2 0 4 2 2 0 2 4 0 4 2 0 2 4 0 3 2 0 4 2 3 0 2 2 0 4 4 0 3 0 2 4 0
```

The minimal test case for this program is b1b1bbb1c1c11111d (which is 98 49 98 49 98 98 98 49 99 49 99 49 49 49 49 49 100 in decimal ascii, which is how the Java interpreter accepts input) which will test all the 23 Turing machine instructions.

---

Brainf\*ck is a tape-based esoteric language created in 1993 by Urban Müller. The sample *Hello World* Sequence program is written based on the Brainf\*ck code included on its [Esolang page](https://esolangs.org/wiki/brainfuck). The Turing machine Sequence code is based on the Brainf\*ck code by [Daniel B Cristofani](http://www.hevanet.com/cristofd/brainfuck/utm.b).

