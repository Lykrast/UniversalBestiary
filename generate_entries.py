import argparse
import generate_base as gb

parser = argparse.ArgumentParser(description='Generate entries for the given mob and put the files in the correct place')
parser.add_argument('registry', help='Registry name of the entity, eg minecraft:zombie')
parser.add_argument('display', help='Display name of the entity, eg Zombie')
parser.add_argument('category', help='Category of the entity, must be a (animals), h (helpers), m (monsters) or b (bosses)')

args = parser.parse_args()

registry = args.registry
display = args.display
category = gb.convertCategory(args.category)

print('Making a {} entry for {} ({})'.format(category, display, registry))

print('Reading templates')

advancement = gb.readTemplate('advancement')
entry = gb.readTemplate('entry')

filename = gb.regToFilename(registry)

print('Filling fields')

advancement = advancement.replace('@MOB@', registry)
entry = entry.replace('@MOB@', registry).replace('@NAME@', display).replace('@CATEGORY@', category).replace('@FILE@', filename)

print('Writing files')

with open('./src/main/resources/assets/universalbestiary/advancements/{}.json'.format(filename), 'w') as file_adv:
	file_adv.write(advancement)
with open('./src/main/resources/assets/universalbestiary/patchouli_books/bestiary/en_us/entries/{}.json'.format(filename), 'w') as file_ent:
	file_ent.write(entry)

print('Done!')